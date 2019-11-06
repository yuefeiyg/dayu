package com.blcultra.queue;

import com.blcultra.dto.LabelDataDto;
import com.lmax.disruptor.RingBuffer;

/**
 * 消息生产者
 * Created by sgy05 on 2019/1/9.
 */
public class LogEventProducer {


    private final RingBuffer<LabelDataDto> ringBuffer;

    public LogEventProducer(RingBuffer<LabelDataDto> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public void onData(LabelDataDto ts){
        //获取下一个序列号
        long sequence = ringBuffer.next();
        try {
            LabelDataDto labelDataDto = ringBuffer.get(sequence);//根据序列号获取分配的数据槽
            //向数据槽中填充数据
            labelDataDto.setDeleteItemIds(ts.getDeleteItemIds());
            labelDataDto.setDeleteRelationIds(ts.getDeleteRelationIds());
            labelDataDto.setInsertItems(ts.getInsertItems());
            labelDataDto.setInsertRelations(ts.getInsertRelations());
            labelDataDto.setTaskid(ts.getTaskid());

        }finally {
            //将发布放在finally代码块中确保发布方法得到调用，
            // 如果某个请求的sequence未被提交，将会阻塞后续的发布操作
            //或者阻塞其他的producer
            ringBuffer.publish(sequence);
        }
    }

}
