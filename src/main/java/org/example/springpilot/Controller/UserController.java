package org.example.springpilot.Controller;

import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.example.springpilot.Service.UserEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserEntryService userEntryService;

    @Autowired
    private UserEntryRepo userEntryRepo;

    @PostMapping
    public void createUser(@RequestBody User user){
        userEntryService.saveNewUser(user);
     }

     @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         assert authentication != null;
         String username = authentication.getName();
         User userInDb = userEntryService.findByUsername(username);
         userInDb.setUsername(user.getUsername());
         userInDb.setPassword(user.getPassword());
         userEntryService.saveUser(userInDb);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteByUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userEntryRepo.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> greeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>("Hi "+authentication.getName(),HttpStatus.OK);
    }
}
