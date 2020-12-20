package com.ipa.sample.weather;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipa.sample.common.AccessStat;
import com.ipa.sample.common.Weather;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedRetryMetrics;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@EnableRetry
@RestController
public class Controller {

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String weatherUri = "http://www.weather.com.cn/data/sk/101021300.html";

    private static final String fakeWeatherUri = "http://www.weather.com.cn/data/sk/101021300";

    private static final String accessStatUri = "http://access-stat/append";

    private static final String countUri = "http://count/";

    private final Counter counter;

    private final Resilience4JCircuitBreakerFactory breakerFactory;

    private Weather defaultWeather;

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Controller(MeterRegistry registry, Resilience4JCircuitBreakerFactory breakerFactory) {
        this.counter = registry.counter("weather-application-get");
        this.breakerFactory = breakerFactory;

        bindMetrics(registry);
    }

    private void bindMetrics(MeterRegistry registry) {
        CircuitBreakerRegistry breakerRegistry = CircuitBreakerRegistry.ofDefaults();
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

        breakerRegistry.circuitBreaker("weather-breaker");
        retryRegistry.retry("weather-retry");

        TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(breakerRegistry).bindTo(registry);
        TaggedRetryMetrics.ofRetryRegistry(retryRegistry).bindTo(registry);
    }

    @GetMapping
    public Weather get() {
        return breakerFactory
                .create("half-crash")
                .run(this::getWeather, throwable -> getDefaultWeather());
    }

    @GetMapping("/test")
    public String getTest() throws MalformedURLException {
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Host", "lifecycle.test.azureappplatform.io");

        ResponseEntity<String> response = template.exchange("https://lifecycle/actuator/health", HttpMethod.GET,
                new HttpEntity<String>(httpHeaders), String.class);

        return response.getBody();
    }

    @SneakyThrows
    protected Weather getWeather() {
        Date now = new Date();
        StopWatch stopwatch = new StopWatch();
        ResponseEntity<String> response;
        Weather weather;

        stopwatch.start();

        switch (count.incrementAndGet() % 3) {
            case 0:
                response = restTemplate.getForEntity(fakeWeatherUri, String.class);
                weather = objectMapper.readValue(response.getBody(), Weather.class);
                break;
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

        this.counter.increment();

        return weather;
    }

    protected Weather getDefaultWeather() {
        if (defaultWeather == null) {
            defaultWeather = new Weather();

            defaultWeather.setWeatherinfo(new Weather.WeatherInfo());
            defaultWeather.getWeatherinfo().setAP("Unknown");
            defaultWeather.getWeatherinfo().setCity("Unknown");
            defaultWeather.getWeatherinfo().setCityId("Unknown");
            defaultWeather.getWeatherinfo().setSD("Unknown");
            defaultWeather.getWeatherinfo().setSm("Unknown");
            defaultWeather.getWeatherinfo().setTemp("Unknown");
            defaultWeather.getWeatherinfo().setTime("Unknown");
            defaultWeather.getWeatherinfo().setWSE("Unknown");
        }

        return defaultWeather;
    }
}

