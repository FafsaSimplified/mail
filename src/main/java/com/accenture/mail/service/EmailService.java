package com.accenture.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendTextMessage(String to, String subject, String text) {
        LOGGER.info("attempt sending an email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendTextMessageFormatted(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        String htmMessage = "<h3>hello world</h3> " +
                String.format("<p>Thank you %s for registering with us</p>", text) +
                "<p>You have successfully created an account.</p>";
        mimeMessageHelper.setText(htmMessage, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        javaMailSender.send(mimeMessage);
    }

    public void sendGreetingEmail(String to, String name) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        String htmMessage = String.format("<h3>Dear %s,</h3>", name) +
                "<p>You may now begin using your FSA ID (username and password) to log in to Federal Student AId (FSA) websites and applications.</p>" +
                "<p>\"If you have any questions or need help, visit \" " +
                "<a href=\"https://fsaid.ed.gov\" target=\"_blank\" " +
                "data-saferedirecturl=\"https://www.google.com/url?q=https://fsaid.ed.gov&amp;source=gmail&amp;ust=1628606066245000&amp;usg=AFQjCNF29nz1GS08Qlmm-6t-Xt_prytfIQ\">" +
                "https://fsaid.ed.gov" +
                "</a>" +
                " \" and select Help for more details.\"</p>" +
                "<p>Thank you,</p>" +
                "<p>U.S. Department of Eduction</p>" +
                "<p>This mailbox is unattended. Please do not reply to this message.</p>";
        mimeMessageHelper.setText(htmMessage, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("FSA ID Created Successfully");
        javaMailSender.send(mimeMessage);
    }

}
