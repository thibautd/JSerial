package dk.thibaut.serial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerialNativeLinux extends SerialNative {

    static {
        try {
            Path libraryFile = SerialNative.extract(
                SerialNativeLinux.class.getClassLoader());
            System.load(libraryFile.toString());
            Files.delete(libraryFile);
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public boolean forCurrentPlatform() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public native String[] getAvailablePortsNames();

}
