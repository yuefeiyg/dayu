package com.blcultra.queue;

import com.blcultra.dto.LabelDataDto;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ThreadFactory;

/**
 * Created by sgy05 on 2019/1/9.
 */
public class DisruptorUtil {
    static Disruptor<LabelDataDto> disruptor = null;
    static{
        LogEventFactory factory = new LogEventFactory();
        int ringBufferSize = 1024;
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable);
            }
        };
        disruptor = new Disruptor<LabelDataDto>(factory, ringBufferSize, threadFactory);
        disruptor.handleEventsWithWorkerPool(new LogEventHandler());
        disruptor.start();
    }

    public static void producer(LabelDataDto ubl){
        RingBuffer<LabelDataDto> ringBuffer = disruptor.getRingBuffer();
        LogEventProducer producer = new LogEventProducer(ringBuffer);
        producer.onData(ubl);
    }


}
