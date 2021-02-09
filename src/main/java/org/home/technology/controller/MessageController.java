package org.home.technology.controller;

import com.amazonaws.services.sqs.model.Message;
import org.apache.camel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MessageController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @PostMapping(value = "/sendMessage")
    public String sendMessage(@RequestBody String requestBody) throws Exception {
        producerTemplate.sendBody("seda:sendRequest", requestBody);
        Endpoint endpoint = camelContext.getEndpoint("seda:requestResponse");
        PollingConsumer consumer = endpoint.createPollingConsumer();
        Exchange exchange = consumer.receive(120000);
        return Objects.requireNonNull(exchange.getIn().getBody(Message.class).getBody(), "Didn't get response back in time");
    }
}
