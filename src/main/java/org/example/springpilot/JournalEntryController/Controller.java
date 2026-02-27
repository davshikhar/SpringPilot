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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class Controller {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserEntryService userEntryService;


    @GetMapping()
    public ResponseEntity<?> getAllEntriesByUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
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
        Authentication authenticaiton = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticaiton.getName();
        User user = userEntryService.findByUsername(username);
        List<JournalEntry> collect=user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> entry = journalEntryService.findById(myId);
            if(entry.isPresent()){
                return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            journalEntryService.saveEntry(myEntry,username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        //deleting the journal entries by id
        boolean removed = journalEntryService.deleteById(myId,username);
        if(removed)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable ObjectId myId,
                                                @RequestBody JournalEntry entry){
        //updating the journalEntries in this endpoint
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userEntryService.findByUsername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());

        if(!collect.isEmpty()){
            Optional<JournalEntry> journalentry = journalEntryService.findById(myId);
            if(journalentry.isPresent()){
                JournalEntry old = journalentry.get();
                old.setTitle(entry.getTitle()!=null && !entry.getTitle().equals("") ? entry.getTitle():old.getTitle());
                old.setContent(entry.getContent()!=null && !entry.getContent().equals("")? entry.getContent() : old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /* for testing the connection
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

     */

}
