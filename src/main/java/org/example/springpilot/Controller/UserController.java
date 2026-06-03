package org.example.springpilot.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springpilot.Entity.User;
import org.example.springpilot.Repository.UserEntryRepo;
import org.example.springpilot.Service.UserEntryService;
import org.example.springpilot.Service.WeatherService;
import org.example.springpilot.api.response.WeatherResponse;
import org.example.springpilot.api.response.localtesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name="User APIs", description = "Read,Update & Delete User")
public class UserController {

    @Autowired
    private UserEntryService userEntryService;

    @Autowired
    private UserEntryRepo userEntryRepo;

    @Autowired
    private WeatherService weatherService;


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
        String username = authentication.getName();

        User user = userEntryService.findByUsername(username);
        String city = (user.getCity() != null && !user.getCity().isEmpty())
                ?user.getCity() : "Lucknow";

        WeatherResponse response = weatherService.getWeather(city);
        String greeting = "";
        if(response!=null){
            greeting = " weather feels " + response.getCurrent().getFeelsLike();
        }
//        return new ResponseEntity<>("Hi " + authentication.getName() + greeting ,HttpStatus.OK);
        return new ResponseEntity<>("Hi " + authentication.getName() ,HttpStatus.OK);
    }

    @PutMapping("/city")
    public ResponseEntity<?> updateCity(@RequestBody Map<String, String> body){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String city = body.get("city");
        userEntryService.updateCity(authentication.getName(), city);
        return ResponseEntity.ok(Map.of("message","city updated"));
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getWeather(@RequestParam String city){
        WeatherResponse response = weatherService.getWeather(city);

        if(response == null){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error","Weather data unavailable"));
        }
        return ResponseEntity.ok(Map.of(
                "city",city,
                "temperature",response.getCurrent().getTemperature(),
                "feelsLike", response.getCurrent().getFeelsLike(),
                "humidity", response.getCurrent().getHumidity(),
                "description", response.getCurrent().getWeatherDescription().get(0)
        ));
    }

    @GetMapping("/list-cities")
    public ResponseEntity<?> getListCities(Authentication authentication){
        User user = userEntryService.findByUsername(authentication.getName());
        return ResponseEntity.ok(Map.of("cities",user.getListCities()));
    }

    @PostMapping("/list-cities")
    public ResponseEntity<?> addCity(@RequestBody Map<String, String> body, Authentication authentication){
        String city = body.get("city");
        userEntryService.addCity(authentication.getName(),city);
        return ResponseEntity.ok(Map.of("message","city added"));
    }

    @DeleteMapping("/list-cities/{city}")
    public ResponseEntity<?> removeCity(@PathVariable String city, Authentication authentication){
        userEntryService.removeCity(authentication.getName(), city);
        return ResponseEntity.ok(Map.of("message","city removed"));
    }

    @GetMapping("/my-weather")
    public ResponseEntity<?> userWeather(Authentication auth) {
        User user = userEntryService.findByUsername(auth.getName());
        String city = (user.getCity() != null && !user.getCity().isEmpty())
                ? user.getCity() : "New Delhi";
        WeatherResponse response = weatherService.getWeather(city);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Weather data unavailable"));
        }
        return ResponseEntity.ok(Map.of(
                "city", city,
                "temperature", response.getCurrent().getTemperature(),
                "feelsLike", response.getCurrent().getFeelsLike(),
                "humidity", response.getCurrent().getHumidity(),
                "description", response.getCurrent().getWeatherDescription().get(0)
        ));
    }

    @GetMapping("/city")
    public ResponseEntity<?> getCity(Authentication auth) {
        User user = userEntryService.findByUsername(auth.getName());
        return ResponseEntity.ok(Map.of("city",
                user.getCity() != null ? user.getCity() : ""));
    }
}
