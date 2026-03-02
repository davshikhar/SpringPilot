package org.example.springpilot.service;

import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.example.springpilot.Service.UserEntryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserEntryRepo userEntryRepo;

    @Autowired
    private UserEntryService userEntryService;


    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    public void testFindByUsername(User user){
        assertTrue(userEntryService.saveNewUser(user));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,2",
            "2,2,4",
            "3,3,9"
    })
    public void test(int a,int b, int expected){
        assertEquals(expected, a+b);
    }
}
