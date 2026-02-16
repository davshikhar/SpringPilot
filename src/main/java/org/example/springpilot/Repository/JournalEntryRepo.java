package org.example.springpilot.Repository;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepo extends MongoRepository <JournalEntry, ObjectId> {

}
