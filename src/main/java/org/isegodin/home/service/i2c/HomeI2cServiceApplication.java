package org.isegodin.home.service.i2c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HomeI2cServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeI2cServiceApplication.class, args);
    }

}
