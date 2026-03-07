package org.example.springpilot.Service;

import org.bson.types.ObjectId;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserEntryService {

    @Autowired
    private UserEntryRepo userEntryRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserEntryService.class);

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
            logger.info("first use of the logger");
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
