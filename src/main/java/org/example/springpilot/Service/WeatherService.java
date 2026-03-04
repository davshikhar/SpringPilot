package org.example.springpilot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
    private static final String apiKey = "ecde3bad8db8d549e1dc08c694c66937";

    private static final String api = "http://api.weatherstack.com/current?api_Key=&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    public String getWeather(String city){
        String finalApi = api.replace("CITY", city).replace("api_Key", apiKey);
        restTemplate.exchange(finalApi, HttpMethod.GET);
    }
}
