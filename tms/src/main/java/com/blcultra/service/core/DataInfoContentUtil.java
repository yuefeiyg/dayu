package com.blcultra.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.AnnotationConstant;
import com.blcultra.cons.AnnotationTemplateConstant;
import com.blcultra.dao.*;
import com.blcultra.util.AnnotationItemsHandle;
import com.blcultra.util.AnnotationLabelParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 进入标注页面时，查询数据并按照不同业务场景将数据进行整合
 */
@Service("dataInfoContentUtil")
public class DataInfoContentUtil {
    private Logger log= LoggerFactory.getLogger(this.getClass());

    private static String sementicbasicinfo ="";

    @Autowired
    AnnotationObjectInfoMapper annotationObjectInfoMapper;
    @Autowired
    AnnotationObjectRelationInfoMapper annotationObjectRelationInfoMapper;
    @Autowired
    AnnotationOtherObjectInfoMapper annotationOtherObjectInfoMapper;
    @Autowired
    DataInfoMapper dataInfoMapper;
    @Autowired
    DataContentMapper dataContentMapper;
    @Autowired
    AnnotationLabelServiceUtil annotationLabelServiceUtil;

    /**
     * 获取文本数据，并整理标注数据
     * @param map
     * @param templatetype
     * @param returnMap
     * @throws Exception
     */
    public void getDataOfText(Map<String, Object> map,String templatetype,Map<String,Object> returnMap) throws Exception{
        this.getLabelTextData(map,returnMap);
        List<Map<String, Object>> contents = (List<Map<String, Object>>)returnMap.get("contents");
        switch (templatetype){
            case AnnotationTemplateConstant.TEMPLATETYPE_STRUCTURE_ANNOTATION://结构标注，单对象属性标注
                removeNotUseKeysBeforeReturn(contents);
                break;
            case AnnotationTemplateConstant.TEMPLATETYPE_SEMANTICS_ANNOTATION: // 语义标注，标注事件等语义要素
                this.getSemanticsContent(contents);
                break;
            case AnnotationTemplateConstant.TEMPLATETYPE_DEPENDENCY_ANNOTATION://依存标注
                this.getDependencyContent(contents);
                removeNotUseKeysBeforeReturn(contents);
                break;
            default:
                return;
        }
    }

    /**
     * 数据返回前，移除没必要的大字段，占用网络传输
     * @param contents
     * @throws Exception
     */
    public void removeNotUseKeysBeforeReturn(List<Map<String, Object>> contents) throws Exception{
        contents.forEach(content -> {
            content.remove("anninfos");
            content.remove("relations");
        });
    }
    /**
     * 根据索引id获取文本数据，并整理标注数据
     * @param map
     * @param templatetype
     * @param returnMap
     * @throws Exception
     */
    public void getDataByIndexIdOfText(Map<String, Object> map,String templatetype,Map<String,Object> returnMap) throws Exception{
        List<Map<String, Object>> contents = new ArrayList<>(1);

        switch (templatetype){
            case AnnotationTemplateConstant.TEMPLATETYPE_STRUCTURE_ANNOTATION://结构标注，单对象属性标注
                contents.add(dataContentMapper.getDataByIndexId(map));
                this.handleSentenceAndLabelDataItemSingle(contents);
                removeNotUseKeysBeforeReturn(contents);
                break;
            case AnnotationTemplateConstant.templatetype_qa : //问答任务
                this.getQAContentByIndex(map,contents,returnMap);
                break;
            case AnnotationTemplateConstant.TEMPLATETYPE_SEMANTICS_ANNOTATION: // 语义标注，标注事件等语义要素
                contents.add(dataContentMapper.getDataByIndexId(map));
                this.getSemanticsContent(contents);
                break;
            case AnnotationTemplateConstant.TEMPLATETYPE_DEPENDENCY_ANNOTATION://依存标注
                contents.add(dataContentMapper.getDataByIndexId(map));
                this.getDependencyContent(contents);
                removeNotUseKeysBeforeReturn(contents);
                break;
            default:
                return;
        }
        returnMap.put("contents", contents);
    }

    /**
     * 处理整理依存标注的数据
     * @param sentencemaps
     * @return
     * @throws Exception
     */
    public void getDependencyContent(List<Map<String,Object>> sentencemaps)throws Exception{
        for(Map<String,Object> sentencemap : sentencemaps){
            /**
             * 查询annotation_other_object_info表，如果存在标注数据，则解析标注数据按照格式组装并返回；
             * 如果不存在标注数据，则为原始数据，找出核心述语，并加以标记，返回数据
             */
            List<Map<String,Object>> labeldatas =  annotationOtherObjectInfoMapper.getLableDataItemList(sentencemap);
            if(null == labeldatas || labeldatas.size() == 0){
                handleSentenceAndLabelDataItem(sentencemap);
                AnnotationItemsHandle.findCorepredicate(sentencemap); // 找出核心述语
                AnnotationItemsHandle.preHandleToFindRelations(sentencemap);
            }else{
                //解析标注数据，并进行解析整理
            }

        }
    }


    /**
     * 查询处理QA问答数据
     * @return
     * @throws Exception
     */
    public void getQAContentByIndex(Map<String, Object> map, List<Map<String, Object>> contents ,Map<String,Object> returnMap)throws Exception{
        Map<String,Object> content =  dataContentMapper.getDataByIndexId(map);
        Map<String,Object> item = annotationOtherObjectInfoMapper.getObjectItemByIndexid(map);
        if(null != item && item.size() > 0){
            String datainfo = item.get("datainfo") + "";
            String itemid = item.get("itemid") + "";
            System.out.println(datainfo);
            JSONObject datainfoJson = JSONObject.parseObject(datainfo);
            datainfoJson.put("itemid",itemid);
            content.put("content",datainfoJson.toJSONString());
            contents.add(content);
            returnMap.put("contents",contents);
        }else{
            //查询标签信息，将qa原始数据中的label找到对应的lableid
            List<Map<String,Object>> labels = annotationLabelServiceUtil.getLables(map);
            Map<String,Object> sentencemap = dataContentMapper.getDataByIndexId(map);
            String contentd = sentencemap.get("content") + "";
            if(contentd.contains("\\\"questions\\\"")){//用于处理json字符串和字符串json
                contentd = contentd.substring(1,contentd.length() -1);
                contentd = contentd.replace("\\\"","\"");
                contentd = contentd.replace("\\\\n","\\n");
                contentd = contentd.replace("\\\\\"","\\\"");
            }
            JSONObject jsonObject = JSONObject.parseObject(contentd);
            JSONArray questions = JSONArray.parseArray(jsonObject.get("questions")+"");
            Iterator qs = questions.iterator();
            while (qs.hasNext()){
                JSONObject question = JSONObject.parseObject(qs.next()+"");
                String qslabel = question.get("label") +"";
                labels.forEach(label ->{
                    if(label.get("labelname").equals(qslabel)){
                        question.put("labelid",label.get("labelid"));
                    }
                });
            }
            sentencemap.put("content",jsonObject.toJSONString());
            contents.add(sentencemap);
            this.handleSentenceAndLabelDataItemSingle(contents);
            removeNotUseKeysBeforeReturn(contents);
            returnMap.put("contents",contents);
        }
    }
    /**
     * 查询处理新华社数据
     * @param sentencemaps
     * @return
     * @throws Exception
     */
    public void getSemanticsContent(List<Map<String,Object>> sentencemaps)throws Exception{
        for(Map<String,Object> sentencemap : sentencemaps){
            //解析content内容
            JSONObject contentjson = JSONObject.parseObject(sentencemap.get("content")+"");
            sentencemap.put("content",contentjson.get("content"));
            //查询标注数据
            List<Map<String,Object>> labeldatas =  annotationOtherObjectInfoMapper.getLableDataItemList(sentencemap);
            if(null == labeldatas || labeldatas.size() == 0){
                synchronized (sementicbasicinfo){
                    JSONObject basicinfo = JSONObject.parseObject(sementicbasicinfo);

                }
                //封装基本信息
                JSONObject basicinfo = new JSONObject();
                basicinfo.put("type","basicinfo");
                basicinfo.put("dataitemid",null);
                JSONArray titles = new JSONArray();
                JSONObject language = new JSONObject();
                language.put("id","language");
                language.put("name","语种");
                language.put("type","text");
                language.put("require",true);
                language.put("editflag",true);
                language.put("placeholder","");
                language.put("index",new ArrayList<>());
                language.put("value","汉语");
                language.put("addbutton",false);
                language.put("deletebutton",false);
                language.put("sequence",1);
                language.put("seleted",false);
                titles.add(language);

                JSONObject title = new JSONObject();
                title.put("id","title");
                title.put("name","标题");
                title.put("type","text");
                title.put("require",true);
                title.put("editflag",true);
                title.put("placeholder","");
                title.put("index",new ArrayList<>());
                title.put("value",contentjson.get("title"));
                title.put("addbutton",false);
                title.put("deletebutton",false);
                title.put("sequence",2);
                title.put("seleted",false);
                titles.add(title);

                JSONObject author = new JSONObject();
                author.put("id","author");
                author.put("name","作者");
                author.put("type","text");
                author.put("require",true);
                author.put("editflag",true);
                author.put("placeholder","划选，可编辑");
                author.put("index",new ArrayList<>());
                author.put("value",contentjson.get("author"));
                author.put("addbutton",false);
                author.put("deletebutton",false);
                author.put("sequence",3);
                author.put("seleted",false);
                titles.add(author);

                JSONObject from = new JSONObject();
                from.put("id","from");
                from.put("name","来源");
                from.put("type","text");
                from.put("require",true);
                from.put("editflag",true);
                from.put("placeholder","");
                from.put("index",new ArrayList<>());
                from.put("value",contentjson.get("from"));
                from.put("addbutton",false);
                from.put("deletebutton",false);
                from.put("sequence",4);
                from.put("seleted",false);
                titles.add(from);

                JSONObject publishtime = new JSONObject();
                publishtime.put("id","publishtime");
                publishtime.put("name","发布时间");
                publishtime.put("type","text");
                publishtime.put("require",true);
                publishtime.put("editflag",true);
                publishtime.put("placeholder","");
                publishtime.put("index",new ArrayList<>());
                publishtime.put("value",contentjson.get("publishtime"));
                publishtime.put("addbutton",false);
                publishtime.put("deletebutton",false);
                publishtime.put("sequence",5);
                publishtime.put("seleted",false);
                titles.add(publishtime);

                JSONObject relation = new JSONObject();
                relation.put("id","relation");
                relation.put("name","关联");
                relation.put("type","text");
                relation.put("require",false);
                relation.put("editflag",true);
                relation.put("placeholder","可编辑");
                relation.put("index",new ArrayList<>());
                relation.put("value","");
                relation.put("addbutton",false);
                relation.put("deletebutton",false);
                relation.put("sequence",6);
                relation.put("seleted",false);
                titles.add(relation);

                JSONObject keyword = new JSONObject();
                JSONArray keywordsarray = new JSONArray();
                String kws = contentjson.get("keyword") +"";
                if(null != kws){
                    String[] kwarray = kws.split(",");
                    for (String kw:kwarray) {
                        JSONObject k = new JSONObject();
                        k.put("name",kw);
                        k.put("index",new ArrayList<>());
                        keywordsarray.add(k);
                    }
                }
                keyword.put("id","keyword");
                keyword.put("name","关键词");
                keyword.put("type","text-span");
                keyword.put("require",true);
                keyword.put("editflag",true);
                keyword.put("placeholder","可编辑");
                keyword.put("index",new ArrayList<>());
                keyword.put("value",keywordsarray);
                keyword.put("addbutton",false);
                keyword.put("deletebutton",false);
                keyword.put("sequence",7);
                keyword.put("seleted",false);
                titles.add(keyword);
            }
            sentencemap.put("items",labeldatas);//标注数据
        }
    }


    /**
     *  查询处理文本信息，每个句子包含标注信息
     * @param sentencemaps
     * @return
     */
    public void handleSentenceAndLabelDataItemSingle(List<Map<String,Object>> sentencemaps){
        for(Map<String,Object> sentencemap : sentencemaps){
            handleSentenceAndLabelDataItem(sentencemap);
        }
    }

    /**
     * 查询处理文本信息，按照篇章、段落层级组装，并且每个句子包含标注信息
     * @param sentencemaps
     * @return
     */
    public List<List<Map<String,Object>>> handleSentenceAndLabelDataItemMulti(List<Map<String,Object>> sentencemaps,Map<String,Object> map){
        List<List<Map<String,Object>>> contentList = new ArrayList<>();
        if(AnnotationConstant.RETURNDATATYPE_SENTENCE.equals(map.get("datatype"))){
            for(Map<String,Object> sentencemap : sentencemaps){
                List<Map<String,Object>> sentences = new ArrayList<>();
                sentences.add(this.handleSentenceAndLabelDataItem(sentencemap));
                contentList.add(sentences);
            }
        }else {
            contentList = handleSentencesContent(sentencemaps,map.get("datatype")+"");
        }

        return contentList;
    }

    /**
     * 查询处理文本信息，按照篇章、段落、句子层级组装，并且每个句子包含标注
     * @param sentencemap
     * @return
     */
    public Map<String,Object> handleSentenceAndLabelDataItem(Map<String,Object> sentencemap){

        //查询标注数据
        List<Map<String,Object>> labeldatas =  annotationObjectInfoMapper.getLableDataItemList(sentencemap);

        labeldatas = new AnnotationLabelParser().sortitems(labeldatas);//对标注标签进行再处理

        List<Map<String,Object>> relatelabeldatas =  annotationObjectRelationInfoMapper.getRelateLableDataItemList(sentencemap);
        sentencemap.put("items",labeldatas);//标注数据
        sentencemap.put("relations",relatelabeldatas);//标注数据
        return sentencemap;

    }


    /**
     * 按照要求组合文本内容
     * @param sentencemaps
     * @param type
     * @return
     */
    public List<List<Map<String,Object>>> handleSentencesContent(List<Map<String,Object>> sentencemaps, String type){
        List<List<Map<String,Object>>> contentList = new ArrayList<>();
        List<Map<String,Object>> content = new ArrayList<>();
        String current = "";
        if (AnnotationConstant.RETURNDATATYPE_PARAGRAPH.equals(type)){
            for(Map<String,Object> sentencemap : sentencemaps){
                if("".equals(current)){
                    current = sentencemap.get("pn")+"";
                }
                if(! current.equals(sentencemap.get("pn")+"")){//多个段落，并且段落发生了变化
                    contentList.add(content);
                    content = new ArrayList<>();
                    current = sentencemap.get("pn")+"";
                }
                content.add(this.handleSentenceAndLabelDataItem(sentencemap));
            }
            contentList.add(content);
        }else if(AnnotationConstant.RETURNDATATYPE_TEXT.equals(type)){
            for(Map<String,Object> sentencemap : sentencemaps){
                if("".equals(current)){
                    current = sentencemap.get("dataid")+"";
                }
                if(! current.equals(sentencemap.get("dataid")+"")){//多个文本，并且文本发生了变化
                    contentList.add(content);
                    content = new ArrayList<>();
                    current = sentencemap.get("dataid")+"";
                }
                content.add(this.handleSentenceAndLabelDataItem(sentencemap));
            }
            contentList.add(content);
        }

        return contentList;
    }


    /**
     * 根据所选的返回数据类型做不同的分页操作
     * @param datasmaps
     * @return
     */
    public Map<String,Object> handleImagePage(List<Map<String,Object>> datasmaps,Map<String,Object> map) throws Exception{
        Map<String,Object> returnmap = new HashMap<>(3);
        List<Map<String,Object>> returndata = new ArrayList<>();
        int pageNow = 0,pageSize = 0;
        if(null !=  map.get("pageNow") && null != map.get("pageSize")){
            pageNow = Integer.parseInt( map.get("pageNow")+"");
            pageSize = Integer.parseInt( map.get("pageSize")+"");
        }
        int total = datasmaps.size();
        if(pageNow == 0 || pageSize == 0){//不分页，全部数据
            returnmap.put("start",0);
            returnmap.put("end",total);
            datasmaps.forEach(imadatamap -> {
                List<Map<String,Object>> items = annotationObjectInfoMapper.getImgItemList(imadatamap);
                List<Map<String,Object>> relateitems = annotationObjectRelationInfoMapper.getPicRelateLableDataItems(imadatamap);
                imadatamap.put("items",items);//标注数据
                imadatamap.put("relations",relateitems);//标注数据
            });
            returndata = datasmaps;
        }else {
            int start = (pageNow - 1) * pageSize;
            int end = pageNow * pageSize ;
            for (; start < end;start ++){
                Map<String,Object> itemmap = datasmaps.get(start);
                List<Map<String,Object>> items = annotationObjectInfoMapper.getImgItemList(itemmap);
                List<Map<String,Object>> relateitems = annotationObjectRelationInfoMapper.getPicRelateLableDataItems(itemmap);
                itemmap.put("items",items);//标注数据
                itemmap.put("relations",relateitems);//标注数据
                returndata.add(itemmap);
            }

        }

        returnmap.put("pageNow",pageNow);
        returnmap.put("pageSize",pageSize);
        returnmap.put("total",total);
        returnmap.put("data",returndata);
        return returnmap;
    }

    /**
     * 组装图像标注的数据
     * @param map
     * @return
     */
    public void  getImageDataByIndexId(Map<String, Object> map,Map<String,Object> returnMap) throws Exception{
        List<Map<String,Object>> datasmaps = dataInfoMapper.getImageDataByIndexId(map);//根据索引查询图片数据
        if(datasmaps != null){
            List<Map<String,Object>> items = new ArrayList<>();
            returnMap.putAll(datasmaps.get(0));
            datasmaps.forEach(data -> {
                data.remove("dataname");
                data.remove("dataid");
                data.remove("datapath");
                data.remove("dataobjecttype");
                data.remove("createtime");
                items.add(data);
            });
            returnMap.put("items",items);
        }
    }


    /**
     * 对比比较改动,对比标注更改的位置
     */
    public void findDifferenceItems(List<Map<String,Object>> contents,List<Map<String,Object>> origincontents){
        boolean editflag ;
        for (int i = 0; i < contents.size(); i++) {
            editflag = false;
            Map<String,Object> content = contents.get(i);
            Map<String,Object> origincontent = origincontents.get(i);
            List<Map<String,Object>> items = (List<Map<String,Object>>) content.get("items");
            List<Map<String,Object>> originitems = (List<Map<String,Object>>) origincontent.get("items");
            if(items.size() == 0 && originitems.size() != 0 || items.size() != 0 && originitems.size() == 0) {
                editflag = true;
            }else{
                for(Map<String,Object> item : items){
                    String labelid = item.get("labelid") + "";
                    String startoffset = item.get("startoffset") + "";
                    String endoffset = item.get("endoffset") + "";
                    boolean sameflag = false;
                    for(Map<String,Object> originitem : originitems){
                        String olabelid = originitem.get("labelid") + "";
                        String ostartoffset = originitem.get("startoffset") + "";
                        String oendoffset = originitem.get("endoffset") + "";
                        if(startoffset.equals(ostartoffset) && endoffset.equals(oendoffset) && labelid.equals(olabelid)){
                            sameflag = true;
                            originitem.put("editflag",false);
                            break;
                        }

                    }
                    if(! sameflag){
                        editflag = true;
                        item.put("editflag",true);
                    }else{
                        item.put("editflag",false);
                    }
                }
            }

            origincontent.put("editflag",editflag);
            content.put("editflag",editflag);
            for(Map<String,Object> item : items){
                if(null == item.get("editflag")){
                    item.put("editflag",true);
                }
            }
            for(Map<String,Object> originitem : originitems){
                if(null == originitem.get("editflag")){
                    originitem.put("editflag",true);
                }
            }

        }
    }

    /**
     * 组装文本标注的数据
     * @param map
     * @return
     */
    public Map<String,Object>  getLabelTextData(Map<String, Object> map,Map<String,Object> returnMap) throws Exception{
        /**
         * 首先根据任务id查询annotation_object_data获取textid（dataid），
         * 然后根据textid查询数据预处理表（目前没有），查询文本处理方式，即全篇存储、分段存储、分句存储，
         * 如果是全篇存储返回数据不做处理，如果是分段存储，可以选择，单段返回或者全篇返回，如果是分句存储，
         * 可以选择按段返回或者按照全文返回。
         * 分页是针对不同返回类型做的，所以不能在数据库中做
         */
        List<Map<String,Object>> sentencemaps = dataContentMapper.getContents(map);//获取所有句子
        if(null != sentencemaps && sentencemaps.size() > 0){

            Map<String,Object> returnmap = handlePage(sentencemaps,map);//处理分页，获取分页后的数据

            if(AnnotationConstant.RETURNCONTENTTYPE_SINGLE.equals(map.get("contenttype"))){//单个矩形框展示
                List<Map<String,Object>> contents =  (List<Map<String, Object>>) returnmap.get("data");
                this.handleSentenceAndLabelDataItemSingle(contents);
                returnMap.put("contents",contents);
            }else if(AnnotationConstant.RETURNCONTENTTYPE_MULTI.equals(map.get("contenttype"))) {//多个矩形框展示
                returnMap.put("contents",
                        this.handleSentenceAndLabelDataItemMulti((List<Map<String, Object>>) returnmap.get("data"),map));
            }

            returnMap.put("pageNow",returnmap.get("pageNow"));
            returnMap.put("pageSize",returnmap.get("pageSize"));
            returnMap.put("total",returnmap.get("total"));
        }else{
            returnMap.put("pageNow",1);
            returnMap.put("pageSize",1);
            returnMap.put("total",0);
        }
        return returnMap;
    }

    /**
     * 根据所选的返回数据类型做不同的分页操作
     * @param sentencemaps
     * @return
     */
    public Map<String,Object> handlePage(List<Map<String,Object>> sentencemaps,Map<String,Object> map) throws Exception{
        Map<String,Object> returnmap = new HashMap<>(3);
        List<Map<String,Object>> returndata = new ArrayList<>();
        int pageNow = 0,pageSize = 0;
        if(null !=  map.get("pageNow") && null != map.get("pageSize")){
            pageNow = Integer.parseInt( map.get("pageNow")+"");
            pageSize = Integer.parseInt( map.get("pageSize")+"");
        }
        int total = HandleSentenceUtil.countNum(sentencemaps,map.get("datatype")+"");

        Map<String,Integer> pagemap = HandleSentenceUtil.caculatePageStartAndEnd(pageNow,pageSize,total);

        returndata =HandleSentenceUtil.getDatas(pagemap.get("start"),pagemap.get("end"),sentencemaps,map.get("datatype")+"");
        returnmap.put("pageNow",pageNow);
        returnmap.put("pageSize",pageSize);
        returnmap.put("total",total);
        returnmap.put("data",returndata);
        return returnmap;
    }
    /**
     * 组装图像标注的数据
     * @param map
     * @return
     */
    public Map<String,Object>  getLabelImageData(Map<String, Object> map) throws Exception{
        Map<String,Object> returnMap = new HashMap<>();
        /**
         * 首先根据任务id查询annotation_object_data获取bndbox（标注坐标），此字段为json
         */
        List<Map<String,Object>> datasmaps = dataInfoMapper.getdatasbytaskid(map);//获取所有句子

        if(datasmaps != null){
            returnMap = this.handleImagePage(datasmaps,map);//处理分页，获取分页后的数据
        }else{
            returnMap.put("pageNow",map.get("pageNow"));
            returnMap.put("pageSize",map.get("pageSize"));
            returnMap.put("total",0);
            returnMap.put("data",null);
        }
        return returnMap;
    }


}
