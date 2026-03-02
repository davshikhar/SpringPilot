package org.example.springpilot.service;

import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.example.springpilot.Service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;

import static org.mockito.Mockito.when;


public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserEntryRepo userEntryRepo;

    //this is done to make sure that all the mocks of this class are intialized and then they are injected
    //in the service
    //so then userEntryRepo will be inialized and then will be injected
    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByNameTest(){
        when(userEntryRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(User.builder().username("vishnu").password("vishnu").roles(new ArrayList<>()).build());
        UserDetails user = userDetailsService.loadUserByUsername("ram");
        Assertions.assertNotNull(user);
    }
}
