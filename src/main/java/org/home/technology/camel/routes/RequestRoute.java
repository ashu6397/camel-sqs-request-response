package org.home.technology.camel.routes;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RequestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:sendRequest?concurrentConsumers=5")
                .process((exchange) -> {
                    System.out.println("Req " + exchange.getIn().getBody());
                    String correlationID = UUID.randomUUID().toString();
                    exchange.getIn().setHeader("AWSCorrelationID", correlationID);
                    exchange.setProperty("AWSCorrelationID", correlationID);
                })
                .to(ExchangePattern.InOnly, "aws-sqs://REQUEST-QUEUE.fifo?amazonSQSClient=#amazonSQSClient&messageDeduplicationIdStrategy=useContentBasedDeduplication&messageGroupIdStrategy=useExchangeId&attributeNames=#AWSCorrelationID")
                .to("seda:fetchResponse?waitForTaskToComplete=Never&timeout=120000");
    }
}
