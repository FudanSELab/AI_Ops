package com.train.test.queue;

import com.train.test.services.RestCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Sink.class)
public class MsgReceiver {

    private final RestCollectService restCollectService;

    @Autowired
    public MsgReceiver(RestCollectService restCollectService) {
        this.restCollectService = restCollectService;
    }

    @StreamListener(Sink.INPUT)
    public void receiveQueueInfo(long requestTime) {
        restCollectService.getCpuMemoryLogInReceiver(requestTime);
    }
}
