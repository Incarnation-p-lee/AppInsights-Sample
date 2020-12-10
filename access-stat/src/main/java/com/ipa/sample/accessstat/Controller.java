package com.ipa.sample.accessstat;

import com.ipa.sample.common.AccessStat;
import com.ipa.sample.common.Weather;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
public class Controller {

    private static final AtomicLong id = new AtomicLong(0);

    private final List<AccessStatEntity> entities = new ArrayList<>();

    private final CircuitBreakerFactory breakerFactory;

    private AccessStat defaultStat;

    public Controller(MeterRegistry registry, CircuitBreakerFactory breakerFactory) {
        this.breakerFactory = breakerFactory;

        bindMetrics(registry);
    }

    private void bindMetrics(MeterRegistry registry) {
        CircuitBreakerRegistry breakerRegistry = CircuitBreakerRegistry.ofDefaults();

        breakerRegistry.circuitBreaker("access-stat-breaker");

        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(breakerRegistry)
                .bindTo(registry);
    }

    @PostMapping("/append")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessStat post(@RequestBody AccessStat stat) throws InterruptedException {
        return breakerFactory
                .create("half-slow")
                .run(() -> appendAccessStat(stat), throwable -> getDefaultStat());
    }

    @SneakyThrows
    protected AccessStat appendAccessStat(AccessStat stat) {
        AccessStatEntity entity = new AccessStatEntity();

        entity.setId(id.incrementAndGet());
        entity.setAccessDate(stat.getAccessDate());
        entity.setTimeCostInMilliSeconds(stat.getTimeCostInMilliSeconds());
        entity.setWeather(stat.getWeather());

        switch (id.intValue() % 3) {
            case 0:
                Thread.sleep(1 * 1000);
            case 1:
                Thread.sleep(5 * 1000);
            default:
                break;
        }

        return stat;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccessStatEntity> list() {
        return entities;
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String count() {
        return String.valueOf(list().size());
    }

    protected AccessStat getDefaultStat() {
        if (defaultStat == null) {
            defaultStat = new AccessStat();

            defaultStat.setWeather(new Weather());
            defaultStat.getWeather().setWeatherinfo(new Weather.WeatherInfo());

            defaultStat.getWeather().getWeatherinfo().setAP("Unknown");
            defaultStat.getWeather().getWeatherinfo().setCity("Unknown");
            defaultStat.getWeather().getWeatherinfo().setCityId("Unknown");
            defaultStat.getWeather().getWeatherinfo().setSD("Unknown");
            defaultStat.getWeather().getWeatherinfo().setSm("Unknown");
            defaultStat.getWeather().getWeatherinfo().setTemp("Unknown");
            defaultStat.getWeather().getWeatherinfo().setTime("Unknown");
            defaultStat.getWeather().getWeatherinfo().setWSE("Unknown");

            defaultStat.setTimeCostInMilliSeconds(0);
            defaultStat.setAccessDate(Date.from(Instant.EPOCH));
        }

        return defaultStat;
    }
}
