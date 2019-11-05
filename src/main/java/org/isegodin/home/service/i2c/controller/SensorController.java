package org.isegodin.home.service.i2c.controller;

import lombok.RequiredArgsConstructor;
import org.isegodin.home.service.i2c.system.sensor.SensorRepository;
import org.isegodin.home.service.i2c.system.sensor.data.SensorInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author isegodin
 */
@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;

    @GetMapping("/list")
    public Flux<SensorInfo> listSensors() {
        return Flux.fromIterable(sensorRepository.listAll());
    }

    @GetMapping("/value/{name}")
    public Mono<Map<String, Object>> getSensorValues(@PathVariable String name) {
        return Mono.just(sensorRepository.getByName(name).readValues());
    }
}
