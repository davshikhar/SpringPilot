package org.example.springpilot.Repository;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserEntryRepo extends MongoRepository<User, ObjectId> {

    User findByUsername(String username);

    void deleteByUsername(String username);

//    List<User> findAll();
}
