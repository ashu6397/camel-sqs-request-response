package org.home.technology.camel.routes;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RequestResponse extends RouteBuilder {

    @Autowired
    private AmazonSQS amazonSQS;

    @Override
    public void configure() throws Exception {
        from("seda:fetchResponse")
                .process((exchange) -> {
                    System.out.println("In processing response");
                    AtomicBoolean isMessageFound = new AtomicBoolean(false);
                    do {
                        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                                .withQueueUrl("RESPONSE-QUEUE.fifo")
                                .withMessageAttributeNames("*")
                                .withAttributeNames("*")
                                .withMaxNumberOfMessages(10)
                                .withWaitTimeSeconds(20);
                        ReceiveMessageResult receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);
                        receiveMessageResult.getMessages().forEach(message -> {
                            String reqAwsCorrelationID = exchange.getProperty("AWSCorrelationID", String.class);
                            String msgAwsCorrelationID = message.getMessageAttributes().get("AWSCorrelationID").getStringValue();
                            if (msgAwsCorrelationID
                                    .equals(reqAwsCorrelationID)) {
                                exchange.getIn().setBody(message);
                                amazonSQS.deleteMessage("RESPONSE-QUEUE.fifo", message.getReceiptHandle());
                                isMessageFound.set(true);
                                return;
                            }
                        });
                    } while (!isMessageFound.get());
                })
                .to("seda:requestResponse");
    }
}
