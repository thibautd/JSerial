package dk.thibaut.serial;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import dk.thibaut.serial.enums.BaudRate;
import dk.thibaut.serial.enums.DataBits;
import dk.thibaut.serial.enums.Parity;
import dk.thibaut.serial.enums.StopBits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;

class SerialPortWindows extends SerialPort {

    static {
        Native.register(SerialPortWindows.class, NativeLibrary.getInstance(
            "JSerial", W32APIOptions.UNICODE_OPTIONS));
    }

    native static Pointer NativeGetErrorString(int error);
    native static Pointer NativeFreeErrorString(Pointer messagePtr);
    native static Pointer NativeOpen(String portName);
    native static boolean NativeRead(Pointer handle, ByteBuffer buffer, int numberOfBytes, IntByReference readBytes);
    native static boolean NativeWrite(Pointer handle, ByteBuffer buffer, int numberOfBytes);
    native static boolean NativeClose(Pointer handle);
    native static int NativeGetBaudRate(Pointer handle);
    native static int NativeGetParity(Pointer handle);
    native static int NativeGetStopBits(Pointer handle);
    native static int NativeGetDataBits(Pointer handle);
    native static void NativeSetBaudRate(Pointer handle, int baudRate);
    native static void NativeSetParity(Pointer handle, int parity);
    native static void NativeSetStopBits(Pointer handle, int stopBits);
    native static void NativeSetDataBits(Pointer handle, int dataBits);
    native static boolean NativeSetConfig(Pointer handle);
    native static boolean NativeSetTimeout(Pointer handle, int timeout);
    native static int NativeGetTimeout(Pointer handle);

    private Pointer handle;
    private SerialChannelWindows channel;

    static SerialException getLastException() {
        int error = Native.getLastError();
        Pointer messagePtr = NativeGetErrorString(error);
        String message = messagePtr.getWideString(0);
        NativeFreeErrorString(messagePtr);
        return new SerialException(error, message);
    }

    SerialPortWindows(String portName) throws SerialException {
        super(portName);
        this.handle = NativeOpen("\\\\.\\" + portName);
        if (this.handle == Pointer.NULL)
            throw getLastException();
        this.channel = new SerialChannelWindows(handle);
        this.inputStream = Channels.newInputStream(channel);
        this.outputStream = Channels.newOutputStream(channel);
    }

    @Override
    public void setConfig(SerialConfig config) throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();

        NativeSetBaudRate(handle, config.BaudRate.toInteger());
        NativeSetDataBits(handle, config.DataBits.toInteger());

        switch (config.Parity) {
            case NONE: NativeSetParity(handle, 0);
                break;
            case ODD: NativeSetParity(handle, 1);
                break;
            case EVEN: NativeSetParity(handle, 2);
                break;
            case MARK: NativeSetParity(handle, 3);
                break;
            case SPACE: NativeSetParity(handle, 4);
                break;
        }

        switch (config.StopBits) {
            case ONE: NativeSetStopBits(handle, 0);
                break;
            case ONE_HALF: NativeSetStopBits(handle, 1);
                break;
            case TWO: NativeSetStopBits(handle, 2);
                break;
        }

        if (!NativeSetConfig(handle))
            throw getLastException();
    }

    @Override
    public SerialConfig getConfig() throws IOException {
        SerialConfig config = new SerialConfig(BaudRate.UNKNOWN,
            Parity.UNKNOWN, StopBits.UNKNOWN, DataBits.UNKNOWN);

        int baudrate = NativeGetBaudRate(handle);
        int parity = NativeGetParity(handle);
        int stopbits = NativeGetStopBits(handle);
        int databits = NativeGetDataBits(handle);

        /* A better solution would be to use a BiMap available
         * in Guava. But I don't won't to add a dependency to
         * Guava. Usually this function is called once or twice,
         * so performance really isn't an issue. */

        config.BaudRate = BaudRate.fromInteger(baudrate);
        config.DataBits = DataBits.fromInteger(databits);

        switch (parity) {
            case 0: config.Parity = Parity.NONE;
                break;
            case 1: config.Parity = Parity.ODD;
                break;
            case 2: config.Parity = Parity.EVEN;
                break;
            case 3: config.Parity = Parity.MARK;
                break;
            case 4: config.Parity = Parity.SPACE;
                break;
        }

        switch (stopbits) {
            case 0: config.StopBits = StopBits.ONE;
                break;
            case 1: config.StopBits = StopBits.ONE_HALF;
                break;
            case 2: config.StopBits = StopBits.TWO;
                break;
        }

        return config;
    }

    @Override
    public void setTimeout(int timeout) throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        if (!NativeSetTimeout(handle, timeout))
            throw getLastException();
    }

    @Override
    public int getTimeout() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return NativeGetTimeout(handle);
    }

    @Override
    public SerialChannel getChannel() throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
        return channel;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
