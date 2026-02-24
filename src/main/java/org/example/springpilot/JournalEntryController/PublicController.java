package org.example.springpilot.JournalEntryController;

import org.example.springpilot.Entity.User;
import org.example.springpilot.Service.UserEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    //this controller is unauthenticated and all the endpoints are open to anyone who tries to access them.

    @Autowired
    private UserEntryService userEntryService;

    @PostMapping("/create-user")
    public void createUser(@RequestBody User user){
        userEntryService.saveNewUser(user);
    }

    @GetMapping("/health-check")
    public String healthCheck(){
        return "ok";
    }
}
