package org.example.springpilot.Repository;

import org.example.springpilot.Entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepo extends MongoRepository<JournalEntry, String> {
}
