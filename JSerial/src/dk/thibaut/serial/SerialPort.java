package dk.thibaut.serial;

import dk.thibaut.serial.enums.BaudRate;
import dk.thibaut.serial.enums.DataBits;
import dk.thibaut.serial.enums.Parity;
import dk.thibaut.serial.enums.StopBits;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
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
     * <p>
     * The port will remain opened until the underlying {@link SerialChannel}
     * is closed, or the {@link #close()} method is called. A closed port cannot
     * be opened again, you must get a new instance using this function.
     *
     * @param portName The platform-specific name of the port.
     * @return An opened {@link SerialPort}.
     * @throws IOException If a problem occurs while opening the port.
     * @throws RuntimeException If the platform is not supported.
     */
    public static SerialPort open(String portName) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows"))
            return new SerialPortWindows(portName);
        throw new RuntimeException("Platform not supported by SerialPort.");
    }

    /**
     * Returns an {@link InputStream} that can be used to read data.
     *
     * @return An InputStream
     * @throws ClosedChannelException If the serial port is closed.
     */
    public InputStream getInputStream() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return Channels.newInputStream(getChannel());
    }

    public OutputStream getOutputStream() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return Channels.newOutputStream(getChannel());
    }

    public void setConfig(BaudRate b, Parity p, StopBits s, DataBits d) throws IOException {
        setConfig(new SerialConfig(b, p, s, d));
    }

    public abstract void setConfig(SerialConfig config) throws IOException;
    public abstract SerialConfig getConfig() throws IOException;
    public abstract void setTimeout(int timeout) throws IOException;
    public abstract int getTimeout() throws IOException;
    public abstract SerialChannel getChannel() throws IOException;

    public abstract boolean isOpen();
    public abstract void close() throws IOException;

    protected String name;
    protected SerialPort(String portName) {
        this.name = name;
    }

}
