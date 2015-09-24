package dk.thibaut.serial;

import java.io.IOException;

public class SerialException extends IOException {

    private int nativeError;

    public SerialException(int nativeError, String message) {
        super(message);
        this.nativeError = nativeError;
    }

    public int getNativeError() {
        return nativeError;
    }
}
