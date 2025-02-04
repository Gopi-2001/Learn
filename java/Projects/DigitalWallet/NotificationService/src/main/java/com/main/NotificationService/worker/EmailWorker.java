package com.main.NotificationService.worker;

import com.main.NotificationService.template.EmailTemplate;
import com.main.common.example.UserIdentifier;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailWorker {
    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmailNotification(String name, String email,
                                      String userIdentifier, String userIdentifierValue) throws MessagingException {

//      This will send  only simple text message in the mail.

//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(email);
//        simpleMailMessage.setText("Hi Welcome to e-wallet");
//        simpleMailMessage.setSubject("Welcome to Wallet");
//        simpleMailMessage.setFrom("randomgopi88@gmail.com");
//
//        javaMailSender.send(simpleMailMessage);

        String mailTemplate = EmailTemplate.getEmailTemplate();
        mailTemplate = mailTemplate.replaceAll("=name=",name);
        mailTemplate = mailTemplate.replaceAll("=documentIdentifier=",userIdentifier);
        mailTemplate = mailTemplate.replaceAll("=documentNo=",userIdentifierValue);


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mail = new MimeMessageHelper(mimeMessage,true);

        mail.setTo(email);
        mail.setFrom("randomgopi88@gmail.com");
        mail.setSubject("Welcome to E-Wallet Application");
        mail.setText(mailTemplate,true);

        javaMailSender.send(mimeMessage);

        System.out.println("Mail Sent to User Successfully");
    }
}
