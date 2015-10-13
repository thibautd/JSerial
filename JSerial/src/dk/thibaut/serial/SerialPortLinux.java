package dk.thibaut.serial;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SerialPortLinux extends SerialPort {

    static {
        Native.register(SerialPortLinux.class, "JSerial");
    }

    static native Pointer NativeGetAvailablePortsNames();
    static native void NativeFreeAvailablePortsNames(Pointer strArray);
    static native Pointer NativeOpen(String portName);

    static List<String> getAvailablePortsNamesImpl() {
        Pointer portsNamesArray = NativeGetAvailablePortsNames();
        List<String> portsNames = new ArrayList<>();
        Collections.addAll(portsNames,
            portsNamesArray.getStringArray(0));
        NativeFreeAvailablePortsNames(portsNamesArray);
        return portsNames;
    }

    private Pointer handle;
    private ByteChannel channel;

    SerialException getLastException() {
        return new SerialException(0, "ERROR");
    }

    SerialPortLinux(String portName) throws IOException {
        super(portName);
        this.handle = NativeOpen(portName);
        if (this.handle == Pointer.NULL)
            throw getLastException();
//        this.channel = null;
//        this.inputStream = Channels.newInputStream(channel);
//        this.outputStream = Channels.newOutputStream(channel);
    }

    @Override
    public void setConfig(SerialConfig config) throws IOException {

    }

    @Override
    public SerialConfig getConfig() throws IOException {
        return null;
    }

    @Override
    public void setTimeout(int timeout) throws IOException {

    }

    @Override
    public int getTimeout() throws IOException {
        return 0;
    }

    @Override
    public SerialChannel getChannel() throws IOException {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setRts(boolean enabled) throws IOException {

    }

    @Override
    public void setDtr(boolean enabled) throws IOException {

    }

    @Override
    public boolean getCts() throws IOException {
        return false;
    }

    @Override
    public boolean getDsr() throws IOException {
        return false;
    }
}
