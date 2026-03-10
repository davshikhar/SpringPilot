package org.example.springpilot.Scheduler;

import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserRepositoryImpl;
import org.example.springpilot.Service.EmailService;
import org.example.springpilot.Service.SentimentAnalysisService;
import org.example.springpilot.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    @Scheduled(cron="0 0 9 * * SUN")
    public void fetchUserAndMail(){
        //it will integrate two things sending mail and fetching the users
        List<User> users = userRepositoryImpl.getUserForSA();
        for (User user:users){
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<String> filterEntries = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getContent()).collect(Collectors.toList());
            //filtered entries based on the last 7 days
            String entry = String.join(" ",filterEntries);
            String sentiment = sentimentAnalysisService.getSentiment(entry);
            emailService.sendEmail(user.getEmail(),"Sentiment for last 7 days",sentiment);
        }
    }

    @Scheduled(cron="0 0/10 * ? * * ")
    public void clearAppCache(){
        //so this entire in memory cache will automatically refresh at 9 AM on every sunday
        //in this case it will refresh after every 10 minutes
        appCache.init();
    }
}
