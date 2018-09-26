package com.train.test.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Source.class)
public class MsgSender {

    private final Source source;

    @Autowired
    public MsgSender(Source source) {
        this.source = source;
    }

    public void sendLoginInfoToSso(long requestTime) {

        source.output().send(MessageBuilder.withPayload(requestTime).build());
    }
}
