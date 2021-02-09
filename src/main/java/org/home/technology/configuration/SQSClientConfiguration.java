package org.home.technology.configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSClientConfiguration {

    @Bean(name = "amazonSQSClient")
    public AmazonSQS getAmazonSQSClient() {
        AmazonSQS amazonSQS = AmazonSQSClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        return amazonSQS;
    }
}
