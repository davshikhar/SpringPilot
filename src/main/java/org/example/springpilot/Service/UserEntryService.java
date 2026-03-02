package org.example.springpilot.Service;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserEntryService {

    @Autowired
    private UserEntryRepo userEntryRepo;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUser(User user){
        userEntryRepo.save(user);
    }

    public boolean saveNewUser(User user){
        try{
            //encoding the password that the user sent and then saving it in the db
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userEntryRepo.save(user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public void saveAdmin(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
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
        return userEntryRepo.findByUsername(username);
    }
}
