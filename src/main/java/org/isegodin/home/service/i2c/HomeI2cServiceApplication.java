package org.isegodin.home.service.i2c;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
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
     *
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    public void overwriteInstanceIp(EurekaInstanceConfigBean configBean, EurekaServiceRegistry serviceRegistry, EurekaRegistration registration) {
        try {
            String ip = resolveExternalIp();

            log.info("Resolved external IP: " + ip);

            configBean.setHostname(ip);
            configBean.setIpAddress(ip);

            serviceRegistry.register(registration);

            System.out.println();

        } catch (Exception e) {
            log.warn("Failed to load external IP address", e);
        }
    }

    private String resolveExternalIp() {
        ClientResponse ipResponse = WebClient.builder().build()
                .get()
                .uri("https://ifconfig.co/ip")
                .exchange()
                .block(Duration.ofSeconds(2));

        return Optional.ofNullable(ipResponse)
                .filter(r -> r.statusCode() == HttpStatus.OK)
                .map(r -> r.toEntity(String.class).block())
                .map(HttpEntity::getBody)
                .map(String::trim)
                .orElseThrow();
    }

}
