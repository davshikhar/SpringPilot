package org.example.springpilot.JournalEntryController;

import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Service.JournalEntryService;
import org.example.springpilot.Service.UserEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class Controller {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserEntryService userEntryService;


    @GetMapping("/{username}")
    public ResponseEntity<?> getAllEntriesByUser(@PathVariable String username){
        User user = userEntryService.findByUsername(username);
        //getting all the entries of the specific user
        List<JournalEntry> entries = user.getJournalEntries();
        if(entries!=null && !entries.isEmpty())
            return new ResponseEntity<>(entries, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntry(@PathVariable ObjectId myId){
        Optional<JournalEntry> entry = journalEntryService.findById(myId);
        if(entry.isPresent()){
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{username}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String username){
        try{
            journalEntryService.saveEntry(myEntry,username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/id/{username}/{myId}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId , @PathVariable String username){
        //deleting the journal entries by id
        journalEntryService.deleteById(myId,username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable ObjectId myId, @RequestBody JournalEntry entry){
        //find the entry which is to be updated
        JournalEntry old = journalEntryService.findById(myId).orElse(null);
        if(old!=null){
            old.setTitle(entry.getTitle()!=null && !entry.getTitle().equals("") ? entry.getTitle() : old.getTitle());
            old.setContent(entry.getContent()!=null && !entry.getContent().equals("") ? entry.getContent() : old.getContent());
//            journalEntryService.saveEntry(old, user);
            return new ResponseEntity<>(old,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
