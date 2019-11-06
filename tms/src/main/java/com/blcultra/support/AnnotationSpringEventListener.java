package com.blcultra.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by sgy05 on 2019/3/6.
 */
@Component
public class AnnotationSpringEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent()==null){
            //初始化动作数据字典（即用户需要的按钮操作）
            ActionsHelper.getInstance().init();
        }
    }
}
