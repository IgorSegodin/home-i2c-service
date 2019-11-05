package org.isegodin.home.service.i2c.system.sensor.impl;

import lombok.Builder;
import lombok.Data;
import org.isegodin.home.service.i2c.system.i2c.I2C;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Spec: https://cdn-shop.adafruit.com/datasheets/BST-BMP180-DS000-09.pdf
 * <p>
 * Lib impl: https://github.com/adafruit/Adafruit-BMP085-Library/blob/master/Adafruit_BMP085.h
 *
 * @author isegodin
 */
public class BMP180Impl {

    private static final int I2C_ADDR = 0x77;

    private static final int CAL_AC5 = 0xB2;
    private static final int CAL_AC6 = 0xB4;
    private static final int CAL_MC = 0xBC;
    private static final int CAL_MD = 0xBE;

    private static final int CAL_DATA_SIZE = 2;

    private static final int CONTROL = 0xF4;
    private static final int CMD_TEMPERATURE = 0x2E;

    private static final int DATA = 0xF6; // starting from most significant byte
    private static final int TEMPERATURE_DATA_SIZE = 2;

    private final I2C device = new I2C(1, I2C_ADDR);

    public SensorData read() {
        try {
            /* Read calibration data */

            byte[] buffer = new byte[4];

            device.readBytes(CAL_AC5, buffer, CAL_DATA_SIZE);
            short AC5 = readShort(buffer);

            device.readBytes(CAL_AC6, buffer, CAL_DATA_SIZE);
            short AC6 = readShort(buffer);

            device.readBytes(CAL_MC, buffer, CAL_DATA_SIZE);
            short MC = readShort(buffer);

            device.readBytes(CAL_MD, buffer, CAL_DATA_SIZE);
            short MD = readShort(buffer);

            /* Read uncompensated temperature */

            device.writeByte(CONTROL, CMD_TEMPERATURE);

            Thread.sleep(5);

            device.readBytes(DATA, buffer, TEMPERATURE_DATA_SIZE);

            short UT = readShort(buffer);

            /* Calc true temperature */

            double X1 = (UT - AC6) * AC5 / Math.pow(2, 15);
            double X2 = MC * Math.pow(2, 11) / (X1 + MD);
            double B5 = X1 + X2;
            double T = (B5 + 8) / Math.pow(2, 4);

            double Tc = T / 10;

            return SensorData.builder()
                    .temperatureCelsius(Tc)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private short readShort(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getShort();
    }

    private byte[] writeShort(short value) {
        byte[] data = new byte[2];

        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putShort(value);

        return buffer.array();
    }

    private int readInt(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt();
    }

    @Data
    @Builder
    public static class SensorData {
        private double temperatureCelsius;
    }
}
