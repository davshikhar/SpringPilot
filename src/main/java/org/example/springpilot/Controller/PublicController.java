package org.example.springpilot.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Service.UserDetailsServiceImpl;
import org.example.springpilot.Service.UserEntryService;
import org.example.springpilot.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name="Public APIs")
public class PublicController {
    //this controller is unauthenticated and all the endpoints are open to anyone who tries to access them.

    @Autowired
    private UserEntryService userEntryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public void signUp(@RequestBody User user){
        userEntryService.saveNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> sigIn(@RequestBody User user){
        try{
            //we will first authenticate the user and then give them the jwt token
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        catch(Exception e){
            log.error(" \u274C Exception came:",e);
            return new ResponseEntity<>(" Incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/health-check")
    public String healthCheck(){
        return "ok";
    }
}
