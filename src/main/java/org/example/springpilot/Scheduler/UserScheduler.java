package org.example.springpilot.Scheduler;

import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserRepositoryImpl;
import org.example.springpilot.Sentiment;
import org.example.springpilot.Service.EmailService;
import org.example.springpilot.Service.SentimentAnalysisService;
import org.example.springpilot.cache.AppCache;
import org.example.springpilot.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

//    @Scheduled(cron="0 0 9 * * SUN")
    public void fetchUserAndMail(){
        //it will integrate two things sending mail and fetching the users
        List<User> users = userRepositoryImpl.getUserForSA();
        for (User user:users){
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getSentiment()).collect(Collectors.toList());
            //getting the list of all the sentiments based on the last 7 days
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for(Sentiment sentiment: sentiments){
                if(sentiment!=null) {
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                }
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for(Map.Entry<Sentiment,Integer> entry: sentimentCounts.entrySet()){
                if(entry.getValue()>maxCount){
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            if(mostFrequentSentiment != null){
                SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days" + mostFrequentSentiment).build();
                kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);/// Data is like sentimentData and key is email
            }
        }
    }

    @Scheduled(cron="0 0/10 * ? * * ")
    public void clearAppCache(){
        //so this entire in memory cache will automatically refresh at 9 AM on every sunday
        //in this case it will refresh after every 10 minutes
        appCache.init();
    }
}
