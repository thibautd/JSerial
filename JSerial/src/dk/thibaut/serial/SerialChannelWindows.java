package dk.thibaut.serial;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;
import java.nio.ByteBuffer;

class SerialChannelWindows implements SerialChannel {

    private Pointer handle;

    SerialChannelWindows(Pointer handle) {
        this.handle = handle;
    }

    public void flush(boolean in, boolean out) throws IOException {
        if (!SerialPortWindows.NativeFlush(handle, in, out))
            throw SerialPortWindows.getLastException();
    }

    public int read(ByteBuffer dst) throws IOException {
        IntByReference readBytesRef = new IntByReference(0);
        if (!SerialPortWindows.NativeRead(handle, dst, dst.remaining(), readBytesRef))
            throw SerialPortWindows.getLastException();
        int readBytes = readBytesRef.getValue();
        dst.position(dst.position() + readBytes);
        return readBytes;
    }

    public int write(ByteBuffer src) throws IOException {
        int toWrite = src.remaining();
        if (!SerialPortWindows.NativeWrite(handle, src, src.remaining()))
            throw SerialPortWindows.getLastException();
        src.position(src.limit());
        return toWrite;
    }

    public boolean isOpen() {
        return handle != null;
    }

    public void close() throws IOException {
        if (!SerialPortWindows.NativeClose(handle))
            throw SerialPortWindows.getLastException();
        handle = null;
    }
}
