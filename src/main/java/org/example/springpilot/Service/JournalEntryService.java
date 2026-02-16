package org.example.springpilot.Service;


import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Repository.JournalEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    public void saveJournalEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }
}
