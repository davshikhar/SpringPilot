package org.example.springpilot.Service;

import org.example.springpilot.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics="weekly-sentiments", groupId = "weekly-sentiment-group"
    ,containerFactory = "kafkaListenerContainerFactory")
    public void consume(SentimentData sentimentData){sendEmail(sentimentData);}
    /// this method will continuously keep listening for the data

    private void sendEmail(SentimentData sentimentData){
        emailService.sendEmail(sentimentData.getEmail(),"Your weekly sentiment summary", buildEmailBody(sentimentData.getSentiment()));
    }

    private String buildEmailBody(String sentiment){
        return "Hi there!\n\n"+"Here's your weekly journal sentiment summary:\n\n"
                +sentiment+"\n\n"+"keep journaling! \n\n"+
                " - SpingPilot Team";
    }
}
