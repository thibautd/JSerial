package dk.thibaut.serial;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class represents a physical serial port.
 * <p>
 * You will never instantiate it directly, instead you must use the
 * {@link #getAvailablePorts} static function to retrieve a list of
 * currently available ports name and get an instance of {@link SerialPort}
 * by calling the {@link #open(String)} static method.
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

    /**
     * Opens a serial port.
     * <p>
     * Internally, this class instantiate a subclass of {@link SerialPort}
     * according to the running platform, and opens the underlying port.
     *
     * @param portName The platform-specific name of the port.
     * @return An opened {@link SerialPort}.
     * @throws IOException If a problem occurs while opening the port.
     */
    public static SerialPort open(String portName) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows"))
            return new SerialPortWindows(portName);
        return null;
    }

    public abstract void setConfig(SerialConfig config) throws IOException;

    public abstract void setTimeout(int timeout) throws IOException;

    public abstract SerialChannel getChannel() throws IOException;

    public abstract boolean isOpen();

    public abstract void close() throws IOException;

    protected String name;

    protected SerialPort(String portName) {
        this.name = name;
    }

}
