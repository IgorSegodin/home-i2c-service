package org.isegodin.home.service.i2c.system.sensor;

import org.isegodin.home.service.i2c.system.sensor.impl.BMP180Impl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author isegodin
 */
@Configuration
public class SensorConfig {

    @Bean
    public SensorRepository sensorRepository(List<SensorApi> apis) {
        return new SensorRepository(apis);
    }

    @Bean
    public SensorApi stubSensor(@Value("${home.sensor.stub.enabled}") boolean enabled) {
        if (!enabled) {
            return null;
        }
        return new SensorApi() {
            @Override
            public String getName() {
                return "Stub";
            }

            @Override
            public String getDescription() {
                return "Stub sensor for test";
            }

            @Override
            public Map<String, Object> readValues() {
                return Map.of("field1", 17);
            }
        };
    }

    @Bean
    public SensorApi bmp180(@Value("${home.sensor.bmp180.enabled}") boolean enabled) {
        if (!enabled) {
            return null;
        }
        return new SensorApi() {

            private BMP180Impl bmp180 = new BMP180Impl();

            @Override
            public String getName() {
                return "BMP180";
            }

            @Override
            public String getDescription() {
                return "Temperature and pressure sensor";
            }

            @Override
            public Map<String, Object> readValues() {
                return Map.of("temperature", bmp180.read().getTemperatureCelsius());
            }
        };
    }
}
