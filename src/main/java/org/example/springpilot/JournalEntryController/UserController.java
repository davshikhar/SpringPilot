package org.example.springpilot.JournalEntryController;

import org.example.springpilot.Entity.User;
import org.example.springpilot.Service.UserEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserEntryService userEntryService;

    @GetMapping
    public List<User> getAllUsers(){
        return userEntryService.getAll();
    }

    @PostMapping
    public void createUser(@RequestBody User user){
        userEntryService.saveUser(user);
     }

     @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
         User userInDb = userEntryService.findByUsername(user.getUsername());
         return new ResponseEntity<>(HttpStatus.OK);
    }
}
