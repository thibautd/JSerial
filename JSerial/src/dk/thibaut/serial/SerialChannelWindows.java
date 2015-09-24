package dk.thibaut.serial;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;
import java.nio.ByteBuffer;

class SerialChannelWindows implements SerialChannel {

    static {
        Native.register(SerialChannelWindows.class, NativeLibrary.getInstance(
            "JSerial", W32APIOptions.UNICODE_OPTIONS));
    }

    private native static boolean NativeRead(Pointer handle, ByteBuffer buffer,
         int numberOfBytes, IntByReference readBytes);
    private native static boolean NativeWrite(Pointer handle, ByteBuffer buffer,
         int numberOfBytes);
    private native static boolean NativeClose(Pointer handle);

    private Pointer handle;

    SerialChannelWindows(Pointer handle) {
        this.handle = handle;
    }

    public void flush(boolean in, boolean out) throws IOException {

    }

    public int read(ByteBuffer dst) throws IOException {
        IntByReference readBytesRef = new IntByReference(0);
        if (!NativeRead(handle, dst, dst.remaining(), readBytesRef))
            throw SerialPortWindows.getLastException();
        int readBytes = readBytesRef.getValue();
        dst.position(dst.position() + readBytes);
        return readBytes;
    }

    public int write(ByteBuffer src) throws IOException {
        int toWrite = src.remaining();
        if (!NativeWrite(handle, src, src.remaining()))
            throw SerialPortWindows.getLastException();
        src.position(src.limit());
        return toWrite;
    }

    public boolean isOpen() {
        return handle != null;
    }

    public void close() throws IOException {
        if (!NativeClose(handle))
            throw SerialPortWindows.getLastException();
        handle = null;
    }
}
