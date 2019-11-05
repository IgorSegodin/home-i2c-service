package org.isegodin.home.service.i2c.system.sensor.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author isegodin
 */
@Data
@Builder
public class SensorInfo {

    private String name;
    private String description;
}
