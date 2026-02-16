package org.example.springpilot.JournalEntryController;

import org.example.springpilot.Entity.JournalEntry;
import org.example.springpilot.Service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return null;
    }

    @GetMapping("/id/{myId}")
    public JournalEntry getJournalEntry(@PathVariable Long myId){
        return null;
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){
        journalEntryService.saveJournalEntry(myEntry);
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
}
