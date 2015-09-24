package dk.thibaut.serial;

import java.io.IOException;
import java.util.List;

/** This class represents a physical serial port.
 * <p>
 * You will never instantiate it directly, instead you must use the
 * {@link #getAvailablePorts} static function to retrieve a list of
 * currently available ports.
 * <p>
 * You can configure the serial connection using {@link #setConfig(SerialConfig)}
 * and {@link #setTimeout}. After calling {@link #open(String)}, you can call
 * {@link #getChannel} to get the associated {@link SerialChannel}, and use it
 * to send and receive data.
 */
public abstract class SerialPort {

    public static final int TIMEOUT_INFINITE = -1;
    public static final int TIMEOUT_IMMEDIATE = 0;

    public static List<String> getAvailablePorts() {
        return null;
    }

    public static SerialPort open(String portName) throws IOException {
        return null;
    }

    private String name;

    SerialPort(String portName) {
        this.name = name;
    }

    public abstract void setConfig(SerialConfig config) throws IOException;

    public abstract void setTimeout(int timeout) throws IOException;

    public abstract SerialChannel getChannel() throws IOException;

}
