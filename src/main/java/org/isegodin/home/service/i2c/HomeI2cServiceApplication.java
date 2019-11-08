package org.isegodin.home.service.i2c;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class HomeI2cServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeI2cServiceApplication.class, args);
    }

    /**
     * Resolve public IP
     */
    @Bean
    public InetUtils inetUtils(InetUtilsProperties inetUtilsProperties) {
        try {
            ClientResponse ipResponse = WebClient.builder().build()
                    .get()
                    .uri("https://ifconfig.co/ip")
                    .exchange()
                    .block(Duration.ofSeconds(2));

            String ip = Optional.ofNullable(ipResponse)
                    .filter(r -> r.statusCode() == HttpStatus.OK)
                    .map(r -> r.toEntity(String.class).block())
                    .map(HttpEntity::getBody)
                    .map(String::trim)
                    .orElseThrow();

            log.info("Resolved external IP: " + ip);

            inetUtilsProperties.setDefaultHostname(ip);
            inetUtilsProperties.setDefaultIpAddress(ip);

        } catch (Exception e) {
            log.warn("Failed to load external IP address", e);
        }

        return new InetUtils(
                inetUtilsProperties
        );
    }

}
