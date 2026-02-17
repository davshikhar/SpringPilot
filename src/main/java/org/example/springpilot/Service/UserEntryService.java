package org.example.springpilot.Service;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserEntryService {

    @Autowired
    private UserEntryRepo userEntryRepo;

    public void saveUser(User user){
        userEntryRepo.save(user);
    }

    public List<User> getAll(){
        return userEntryRepo.findAll();
    }

    public Optional<User> findById(ObjectId id){
        return userEntryRepo.findById(id);
    }

    public void deleteById(ObjectId id){
        userEntryRepo.deleteById(id);
    }

    public User findByUsername(String username){
        return userEntryRepo.findByUserName(username);
    }
}
