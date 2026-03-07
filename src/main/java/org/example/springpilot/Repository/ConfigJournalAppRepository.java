package org.example.springpilot.Repository;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.ConfigJournalAppEntity;
import org.example.springpilot.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalAppEntity, ObjectId> {

}
