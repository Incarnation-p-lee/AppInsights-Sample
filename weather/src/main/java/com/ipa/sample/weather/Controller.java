package com.ipa.sample.weather;

import com.ipa.sample.common.AccessStat;
import com.ipa.sample.common.Weather;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
public class Controller {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String weatherUri = "http://www.weather.com.cn/data/sk/101021300.html";

    private static final String accessStatUri = "http://access-stat/append";

    @GetMapping
    public Weather get() {
        Date now = new Date();
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        Weather weather = restTemplate.getForObject(weatherUri, Weather.class);

        stopwatch.stop();

        AccessStat stat = new AccessStat();

        stat.setAccessDate(now);
        stat.setTimeCostInMilliSeconds(stopwatch.getTotalTimeMillis());
        stat.setWeather(weather);

        restTemplate.postForEntity(accessStatUri, stat, AccessStat.class);

        return weather;
    }
}
