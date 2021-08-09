package com.accenture.mail.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SQSService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQSService.class);
    private int shortDelay = 35;
    private int longDelay = 20000;
    private int delay = 35;
    private final AmazonSQS amazonSQS;
    private final String endpoint;
    private final ReceiveMessageRequest receiveMessageRequest;
    private final EmailService emailService;

    @Autowired
    public SQSService(AmazonSQS amazonSQS,
                      @Value("${sqs.end-point.url}") String endpoint, EmailService emailService) {
        this.amazonSQS = amazonSQS;
        this.endpoint = endpoint;
        this.emailService = emailService;
        this.receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(this.endpoint)
                .withMessageAttributeNames("All")
                .withWaitTimeSeconds(20) // long polling
                .withVisibilityTimeout(30) // in flight
                .withMaxNumberOfMessages(5); // poll max 10 messages
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private int counter = 1;

    public void poll() {
        LOGGER.info(counter++ + ": Time " +
                LocalTime.now().format(DateTimeFormatter.ISO_TIME) + " waited " + this.getDelay() + " ms.");
        List<Message> messages = amazonSQS.receiveMessage(this.receiveMessageRequest).getMessages();
        if (!messages.isEmpty()) {
            for (Message m : messages) {
                String name = m.getMessageAttributes().get("Name").getStringValue();
                String email = m.getBody();
                LOGGER.info("Attempt sending an email to recipient: " + name + " at " + email);
                try {
                    this.emailService.sendGreetingEmail(email, name);
                    amazonSQS.deleteMessage(this.endpoint, m.getReceiptHandle());
                } catch (MessagingException e) {
                    LOGGER.error("Failed sending an email to " + m.getBody());
                    LOGGER.error(e.getMessage());
                }
            }
            setDelay(this.shortDelay);
        } else {
            setDelay(this.longDelay);
        }
    }
}
