package dk.thibaut.serial;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

class SerialPortWindows extends SerialPort {

    static {
        Native.register(SerialPortWindows.class, NativeLibrary.getInstance(
            "JSerial", W32APIOptions.UNICODE_OPTIONS));
    }

    private native static Pointer NativeGetErrorString(int error);
    private native static Pointer NativeFreeErrorString(Pointer messagePtr);
    private native static Pointer NativeOpen(String portName);

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
        this.handle = NativeOpen(portName);
        if (this.handle == Pointer.NULL)
            throw getLastException();
        this.channel = new SerialChannelWindows(handle);
    }

    @Override
    public void setConfig(SerialConfig config) throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
    }

    @Override
    public void setTimeout(int timeout) throws IOException {
        if (!isOpen())
            throw new ClosedChannelException();
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
