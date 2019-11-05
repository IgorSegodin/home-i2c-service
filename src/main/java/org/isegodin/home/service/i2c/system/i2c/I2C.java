package org.isegodin.home.service.i2c.system.i2c;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * @author ihor.lavryniuk
 */
public class I2C {

    private final int bus;
    private final int deviceAddress;

    public I2C(int bus, int deviceAddress) {
        this.bus = bus;
        this.deviceAddress = deviceAddress;
    }

    public byte[] readBytes(int memoryAddress, int length) {
        byte[] result = new byte[length];
        int position = 0;
        for (int i = memoryAddress; i < memoryAddress + length; i++) {
            result[position] = (byte) readByte(i);
            position++;
        }
        return result;
    }

    public void readBytes(int memoryAddress, byte[] buffer, int length) {
        if (buffer == null) {
            throw new RuntimeException("Buffer not initialized");
        }
        if (buffer.length < length) {
            throw new RuntimeException("Buffer size cannot be smaller than length of requested data");
        }
        byte[] data = readBytes(memoryAddress, length);
        if (length >= 0) {
            System.arraycopy(data, 0, buffer, 0, length);
        }
    }

    public int readByte(int memoryAddress) {
        String cmd = "i2cget -f -y " + Integer.toString(bus) + " 0x" + Integer.toHexString(deviceAddress) + " 0x" + Integer.toHexString(memoryAddress);
        return Integer.parseInt(exec(cmd).findFirst().get().substring(2), 16);
    }

    public void write(byte[] buffer, int length) {
        for (int position = 0; position < length; position++) {
            writeByte(position, buffer[position]);
        }
    }

    public void writeByte(int address, int command) {
        String cmd = "i2cset -f -y " + bus + " 0x" + Integer.toHexString(deviceAddress) + " 0x" + Integer.toHexString(address) + " 0x" + Integer.toHexString(command);
        exec(cmd);
    }

    private static Stream<String> exec(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
