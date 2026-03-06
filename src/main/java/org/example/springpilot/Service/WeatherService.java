package org.example.springpilot.Service;

import org.example.springpilot.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
//    @Value("${env.API_KEY}")
    private static final String apiKey = "ecde3bad8db8d549e1dc08c694c66937";

    private static final String api = "http://api.weatherstack.com/current?access_key=apiKey&query=CITY";

    /*api written below for local testing
    private static final String api2 = "http://localhost:3000/weather";
     */

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city){
        String finalApi = api.replace("CITY", city).replace("apiKey", apiKey);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body = response.getBody();
        return body;
        /*
        localtesting test = localtesting.builder().city(city).build();
        HttpEntity httpEntity = new HttpEntity<>(test);
        ResponseEntity<localtesting> res = restTemplate.exchange(api2,HttpMethod.POST, httpEntity, localtesting.class);
        localtesting body1 = res.getBody();
         */

        //        to do a post call on api we can do by:-
        /*
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("key","value");
        User user = User.builder().username("ram").password("ram").build();
        HttpEntity httpEntity = new HttpEntity<>(user,httpHeaders);
        and then we can pass all the data like this
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.POST, httpEntity, WeatherResponse.class);
         */
    }
}
