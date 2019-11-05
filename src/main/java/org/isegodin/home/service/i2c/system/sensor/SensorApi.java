package org.isegodin.home.service.i2c.system.sensor;

import java.util.Map;

/**
 * @author isegodin
 */
public interface SensorApi {

    String getName();

    String getDescription();

    Map<String, Object> readValues();
}
