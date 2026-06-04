package org.example.springpilot.Scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
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
@Slf4j
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AppCache appCache;

    @Autowired(required = false)
    private KafkaTemplate<String, SentimentData> kafkaTemplate;
    @Autowired
    private UserEntryRepo userEntryRepo;

    //    @Scheduled(cron="0 0 9 * * SUN")
    @Scheduled(cron="0 0/1 * ? * *")
    public void fetchUserAndMail(){
//        List<User> users = userRepositoryImpl.getUserForSA();
        List<User> users = userEntryRepo.findAll();
        log.info("Scheduler running, users found: {}", users.size());// for debugging
        for(User user:users){
            if(user.getEmail() == null || user.getEmail().isEmpty())
                continue;
            List<JournalEntry> journalEntries = user.getJournalEntries();
            if(journalEntries.isEmpty()){
                continue;
            }
            List<Sentiment> sentiments = journalEntries.stream()
//                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7,ChronoUnit.DAYS)))
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(10,ChronoUnit.MINUTES)))
                    .map(JournalEntry::getSentiment)
                    .filter(s -> s!=null)
                    .collect(Collectors.toList());

            log.info("Sentiments found for {}: {}", user.getEmail(), sentiments.size());//for debugging

            if(sentiments.isEmpty())
                continue;

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for(Sentiment sentiment: sentiments){
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0)+1);
            }

            Sentiment mostFrequent = sentimentCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            if(mostFrequent!=null){
                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Your most frequent sentiment this week was: "+mostFrequent)
                        .build();
                try{
                    kafkaTemplate.send("weekly-sentiments",sentimentData.getEmail(),sentimentData);
                }
                catch(Exception e){
                    // this is kafka fallback is kafka fails
                    emailService.sendEmail(sentimentData.getEmail(),
                            "Your weekly sentiment summary",
                            sentimentData.getSentiment());
                }
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
