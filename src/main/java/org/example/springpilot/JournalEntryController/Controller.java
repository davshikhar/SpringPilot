package org.example.springpilot.JournalEntryController;

import jakarta.annotation.PostConstruct;
import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")
public class Controller {

    @Autowired
    private JournalEntryService journalEntryService;


    @GetMapping
    public List<JournalEntry> getAll(){
        return journalEntryService.getAll();
    }

    @GetMapping("/id/{myId}")
    public JournalEntry getJournalEntry(@PathVariable Long myId){
        return null;
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){
        myEntry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry(myEntry);
        return true;
    }

    @DeleteMapping("/id/{myId}")
    public JournalEntry deleteJournalEntry(@PathVariable Long myId){
        return null;
    }

    @PutMapping("/id/{myId}")
    public JournalEntry updateJournalEntry(@PathVariable Long myId, @RequestBody JournalEntry entry){
        return null;
    }

    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @GetMapping("/db")
    public String getDbName() {
        return mongoTemplate.getDb().getName();
    }

    @PostConstruct
    public void printDb() {
        System.out.println("Connected DB: " + mongoTemplate.getDb().getName());
    }

}
