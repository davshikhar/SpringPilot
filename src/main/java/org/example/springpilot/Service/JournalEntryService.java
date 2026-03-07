package org.example.springpilot.Service;


import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.JournalEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private UserEntryService userEntryService;

    @Transactional
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
            throw new RuntimeException("An error occurred while saving the entry.",e);
        }
    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepo.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean removed = false;
        try{
            User user = userEntryService.findByUsername(username);
            //removing the deleted journal id from the user's journal entries list
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if(removed){
                //saving the user back to the db
                userEntryService.saveUser(user);
                journalEntryRepo.deleteById(id);
            }
        }
        catch(Exception e){
            log.error("Error:",e);
            throw new RuntimeException("An error occurred while deleting the entry",e);
        }
        return removed;
    }
}
