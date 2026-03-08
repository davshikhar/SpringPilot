package org.example.springpilot.Service;

import org.example.springpilot.api.response.WeatherResponse;
import org.example.springpilot.cache.AppCache;
import org.example.springpilot.constants.PlaceHolders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;

//    private static final String api = "http://api.weatherstack.com/current?access_key=apiKey&query=CITY";
//we will ask for the api from the mongodb
    /*api written below for local testing
    private static final String api2 = "http://localhost:3000/weather";
     */

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city){
//        String finalApi = api.replace("CITY", city).replace("apiKey", apiKey);
        String finalApi = appCache.appCache.get(AppCache.keys.WEATHER_API).replace(PlaceHolders.CITY, city).replace(PlaceHolders.API_KEY, apiKey);
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
