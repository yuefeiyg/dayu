package com.blcultra.queue;

import com.blcultra.dto.LabelDataDto;
import com.blcultra.model.UserBehaviorLogWithBLOBs;
import com.lmax.disruptor.EventFactory;

/**
 * Created by sgy05 on 2019/1/9.
 */
public class LogEventFactory implements EventFactory<LabelDataDto> {
    @Override
    public LabelDataDto newInstance() {
        return new LabelDataDto();
    }
}
