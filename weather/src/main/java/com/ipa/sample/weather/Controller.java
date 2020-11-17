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
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class Controller {

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String weatherUri = "http://www.weather.com.cn/data/sk/101021300.html";

    private static final String fakeWeatherUri = "http://www.weather.com.cn/data/sk/101021300";

    private static final String accessStatUri = "http://access-stat/append";

    private static final String countUri = "http://count/";

    @GetMapping
    public Weather get() throws JsonProcessingException, InterruptedException {
        Date now = new Date();
        StopWatch stopwatch = new StopWatch();
        ResponseEntity<String> response;
        Weather weather;

        stopwatch.start();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        switch (count.incrementAndGet() % 3) {
            case 0:
                response = restTemplate.getForEntity(fakeWeatherUri, String.class);
                weather = objectMapper.readValue(response.getBody(), Weather.class);
                break;
            case 2:
                Thread.sleep(1000 * 10);
            case 1:
            default:
                response = restTemplate.getForEntity(weatherUri, String.class);
                weather = objectMapper.readValue(response.getBody(), Weather.class);
                break;
        }

        stopwatch.stop();

        AccessStat stat = new AccessStat();

        stat.setAccessDate(now);
        stat.setTimeCostInMilliSeconds(stopwatch.getTotalTimeMillis());
        stat.setWeather(weather);

        restTemplate.postForEntity(accessStatUri, stat, AccessStat.class);

        restTemplate.getForEntity(countUri, String.class);

        return weather;
    }
}
