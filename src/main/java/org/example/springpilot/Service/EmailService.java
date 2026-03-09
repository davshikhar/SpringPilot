package org.example.springpilot.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    //this service class will be used to send mails to the users
    //related to the sentiments corresponding to that

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body){
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
        }
        catch(Exception e){
            log.error("Exception while sending the mail:",e);
        }
    }
}
