package org.home.technology.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ResponseRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("aws-sqs://REQUEST-QUEUE.fifo?amazonSQSClient=#amazonSQSClient&messageGroupIdStrategy=useExchangeId&attributeNames=*&messageAttributeNames=*&concurrentConsumers=5")
                .setBody(simple("${in.body} this is response"))
                .log("Response sent back ${headers}")
                .to("aws-sqs://RESPONSE-QUEUE.fifo?amazonSQSClient=#amazonSQSClient&messageGroupIdStrategy=useExchangeId");
    }
}
