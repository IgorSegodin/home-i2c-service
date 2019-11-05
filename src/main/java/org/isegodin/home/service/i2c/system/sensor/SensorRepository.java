package org.isegodin.home.service.i2c.system.sensor;


import org.isegodin.home.service.i2c.system.sensor.data.SensorInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author isegodin
 */
public class SensorRepository {

    private final Map<String, SensorApi> sensorMap;

    public SensorRepository(List<SensorApi> apis) {
        this.sensorMap = apis.stream()
                .collect(Collectors.toMap(SensorApi::getName, s -> s));
    }

    public List<SensorInfo> listAll() {
        return sensorMap.values().stream()
                .map(s -> SensorInfo.builder()
                        .name(s.getName())
                        .description(s.getDescription())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public SensorApi getByName(String name) {
        return sensorMap.get(name);
    }
}
