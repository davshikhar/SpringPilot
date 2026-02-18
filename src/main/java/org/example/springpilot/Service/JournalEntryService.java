package org.example.springpilot.Service;


import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.JournalEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private UserEntryService userEntryService;

    public void saveEntry(JournalEntry journalEntry, String username){
        try{
            User user = userEntryService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved =journalEntryRepo.save(journalEntry);//extracting the saved entry in the db in the variable
            user.getJournalEntries().add(saved);//adding the saved entry to user entry list
            userEntryService.saveUser(user);//finally saving the user in the database.
        }
        catch(Exception e){
            log.error("Exception :",e);
        }
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepo.findById(id);
    }

    public void deleteById(ObjectId id, String username){
        User user = userEntryService.findByUsername(username);
        //removing the deleted journal id from the user's journal entries list
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));

        //saving the user back to the db
        userEntryService.saveUser(user);
        journalEntryRepo.deleteById(id);
    }
}
