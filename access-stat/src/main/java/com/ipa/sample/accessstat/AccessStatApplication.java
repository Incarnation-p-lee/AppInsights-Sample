package com.ipa.sample.accessstat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class AccessStatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
