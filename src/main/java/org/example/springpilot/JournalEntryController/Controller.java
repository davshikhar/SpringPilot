package org.example.springpilot.JournalEntryController;

import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
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
    public JournalEntry getJournalEntry(@PathVariable ObjectId myId){
        return journalEntryService.findById(myId).orElse(null);
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){
        myEntry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry(myEntry);
        return true;
    }

    @DeleteMapping("/id/{myId}")
    public boolean deleteJournalEntry(@PathVariable ObjectId myId){
        //deleting the journal entries by id
        journalEntryService.deleteById(myId);
        return true;
    }

    @PutMapping("/id/{myId}")
    public JournalEntry updateJournalEntry(@PathVariable ObjectId myId, @RequestBody JournalEntry entry){
        //find the entry which is to be updated
        JournalEntry old = journalEntryService.findById(myId).orElse(null);
        if(old!=null){
            old.setTitle(entry.getTitle()!=null && !entry.getTitle().equals("") ? entry.getTitle() : old.getTitle());
            old.setContent(entry.getContent()!=null && !entry.getContent().equals("") ? entry.getContent() : old.getContent());
        }
        journalEntryService.saveEntry(old);
        return old;
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
