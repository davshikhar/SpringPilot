package org.example.springpilot.service;

import org.example.springpilot.Service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail(){
        emailService.sendEmail("shikharyadav231@gmail.com","Testing java mail sender","Hi, how are you?");
    }
}
