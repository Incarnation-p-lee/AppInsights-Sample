package com.ipa.sample.accessstat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AccessStatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessStatApplication.class, args);
    }
}
