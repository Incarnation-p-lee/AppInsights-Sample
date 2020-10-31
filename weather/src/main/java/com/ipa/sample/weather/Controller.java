package com.ipa.sample.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipa.sample.common.AccessStat;
import com.ipa.sample.common.Weather;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
public class Controller {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String weatherUri = "http://www.weather.com.cn/data/sk/101021300.html";

    private static final String accessStatUri = "http://access-stat/append";

    @GetMapping
    public Weather get() throws JsonProcessingException {
        Date now = new Date();
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseEntity<String> response = restTemplate.getForEntity(weatherUri, String.class);
        Weather weather = objectMapper.readValue(response.getBody(), Weather.class);

        stopwatch.stop();

        AccessStat stat = new AccessStat();

        stat.setAccessDate(now);
        stat.setTimeCostInMilliSeconds(stopwatch.getTotalTimeMillis());
        stat.setWeather(weather);

        restTemplate.postForEntity(accessStatUri, stat, AccessStat.class);

        return weather;
    }
}
