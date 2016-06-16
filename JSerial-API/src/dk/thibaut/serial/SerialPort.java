package dk.thibaut.serial;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.ClosedChannelException;
import java.util.*;

/**
 * This class represents a physical serial port.
 * <p>
 * You will never instantiate it directly, instead you must use the
 * {@link #getAvailablePortsNames} static function to retrieve a list of
 * currently available ports name and get an instance of {@link SerialPort}
 * by calling the {@link #open(String)} static method.
 * <p>
 * You can configure the serial connection using {@link #setConfig(SerialConfig)}
 * and {@link #setTimeout}. After calling {@link #open(String)}, you can call
 * {@link #getChannel} to get the associated {@link SerialChannel}, and use it
 * to send and receive data. If you prefer to use classic Java streams, both
 * {@link #getOutputStream()} and {@link #getInputStream()} functions
 * are available.
 */
public class SerialPort {

    public static final int TIMEOUT_INFINITE = -1;
    public static final int TIMEOUT_IMMEDIATE = 0;

    private static SerialNative serialNative;

    static {
        ServiceLoader<SerialNative> loader = ServiceLoader.load(SerialNative.class);
        for (SerialNative serialNative : loader) {
            if (serialNative.forCurrentPlatform()) {
                SerialPort.serialNative = serialNative;
                break;
            }
        }
        if (SerialPort.serialNative == null) {
            throw new RuntimeException("No serial implementation for the current platform.");
        }
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private String name;

    private SerialPort(String portName) {
        this.name = portName;
    }

    /**
     * Get the list of available serial ports on the system.
     * <p>
     * Returned port names are platform-specific, for example COMXX on Windows,
     * and /dev/ttyXX on Linux.
     *
     * @return A list of ports names.
     */
    public static List<String> getAvailablePortsNames() {
        return Arrays.asList(serialNative.getAvailablePortsNames());
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
        SerialPort serialPort = new SerialPort(portName);
        //serialNative.open(serialPort, portName);
        return serialPort;
    }

    /**
     * Get the port name.
     * <p>
     * @return The value passed to {@link SerialPort#open(String)}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get an {@link InputStream} that can be used to read data from the port.
     *
     * @return An InputStream object, wrapping the native {@link SerialChannel}
     * @throws ClosedChannelException If the serial port is closed.
     */
    public InputStream getInputStream() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return inputStream;
    }

    /**
     * Get an {@link OutputStream} that can be used to write data to the port.
     *
     * @return An OutputStream object, wrapping the native {@link SerialChannel}
     * @throws ClosedChannelException If the serial port is closed.
     */
    public OutputStream getOutputStream() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return outputStream;
    }

    /**
     * A shortcut to {@link #setConfig(SerialConfig)}.
     *
     * @param b Baudrate setting
     * @param p Parity setting
     * @param s Stopbits setting
     * @param d DataBits setting
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public void setConfig(BaudRate b, Parity p, StopBits s, DataBits d) throws IOException {
        setConfig(new SerialConfig(b, p, s, d));
    }

    /**
     * Sets the configuration to use for this port.
     *
     * @param config The configuration to apply.
     * @see #setConfig(BaudRate, Parity, StopBits, DataBits)
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public void setConfig(SerialConfig config) throws IOException {

    }

    /**
     * Returns the current configuration of this port.
     *
     * @return A SerialConfig object containing current settings.
     * @throws IOException If an error occurs when calling the native function.
     */
    public SerialConfig getConfig() throws IOException {
        return null;
    }

    /**
     * Sets the read timeout for this port.
     * <p>
     * This function supports two special values:
     * <ul>
     *  <li>{@link #TIMEOUT_INFINITE}: Read calls won't return until some bytes are available.</li>
     *  <li>{@link #TIMEOUT_IMMEDIATE}: Read calls will return immediately, even if no bytes are available.</li>
     * </ul>
     *
     * @param timeout The timeout value, in milliseconds.
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public void setTimeout(int timeout) throws IOException {

    }

    /**
     * Get the current read timeout.
     * <p>
     * The default timeout is always {@link #TIMEOUT_INFINITE}.
     *
     * @return The timeout (in milliseconds) or one of the special
     *      values {@link #TIMEOUT_INFINITE} or {@link #TIMEOUT_IMMEDIATE}
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public int getTimeout() throws IOException {
        return TIMEOUT_IMMEDIATE;
    }

    /**
     * Get the associated {@link SerialChannel} that can used to read and write data.
     * <p>
     * If you close this channel, the port will also be considered closed.
     *
     * @return A unique instance of {@link SerialChannel}
     * @throws ClosedChannelException If the serial port is closed.
     */
    public SerialChannel getChannel() throws IOException {
        return null;
    }

    /**
     * Get the current opening status of the port.
     * <p>
     * The port is always opened when you create it. It will be closed only if you
     * call {@link #close()} or close the underlying {@link SerialChannel} or streams.
     *
     * @return True is the port is opened, false if it has been close.
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * Close the port.
     * <p>
     * After closing the port, you can't re-open it, and you should not use it.
     *
     * @throws IOException If an error occurs when calling the native function.
     */
    public void close() throws IOException {

    }

    /**
     * Set the RTS (Request To Send) signal.
     *
     * @param enabled The status of the line.
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public void setRts(boolean enabled) throws IOException {

    }

    /**
     * Set the DTR (Data Terminal Ready) signal.
     *
     * @param enabled The status of the line.
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public void setDtr(boolean enabled) throws IOException {

    }

    /**
     * Get the CTS (Clear to send) signal's state.
     *
     * @return The status of the CTS pin.
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public boolean getCts() throws IOException {
        return false;
    }

    /**
     * Get the DSR (Data set ready) signal's state.
     *
     * @return The status of the DSR pin.
     * @throws IOException If an error occurs when calling the native function.
     * @throws ClosedChannelException If the serial port is closed.
     */
    public boolean getDsr() throws IOException {
        return false;
    }

}
