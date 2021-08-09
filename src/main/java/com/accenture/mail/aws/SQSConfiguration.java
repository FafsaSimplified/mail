package com.accenture.mail.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQSConfiguration.class);

    private AmazonSQSAsync buildAmazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .build();
    }

    @Bean
    public AmazonSQS getAmazonSQS() {
        return AmazonSQSClientBuilder.defaultClient();
    }
}
