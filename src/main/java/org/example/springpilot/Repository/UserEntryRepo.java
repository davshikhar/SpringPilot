package org.example.springpilot.Repository;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEntryRepo extends MongoRepository<User, ObjectId> {

    User findByUserName(String username);
}
