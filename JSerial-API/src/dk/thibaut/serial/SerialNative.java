package dk.thibaut.serial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class SerialNative {

    /**
     * Extracts the specified native DLL to a temporary directory.
     * <p>
     * Because {@link System#load(String)} requires a path on the file
     * system, we have to copy the native library available in JAR
     * to a temporary directory before loading it.
     *
     * @param classLoader The ClassLoader to use for opening the library in JAR.
     * @return The path of the extracted file.
     * @throws IOException If an I/O error occurs.
     */
    public static Path extract(ClassLoader classLoader) throws IOException {
        String arch = System.getProperty("os.arch");
        String file = arch + File.separator + System.mapLibraryName("JSerial");
        Path tempDir = Files.createTempDirectory("JSerial");
        Path tempFile = tempDir.resolve(System.mapLibraryName("JSerial"));
        try (InputStream stream = classLoader.getResourceAsStream(file)) {
            if (stream == null)
                throw new RuntimeException("Missing library file: " + file);
            Files.copy(stream, tempFile);
        }
        return tempFile;
    }

    /**
     * This method must return true if this native
     * implementation matches the current platform.
     *
     * @return <code>true</code> or <code>false</code>
     */
    abstract boolean forCurrentPlatform();

    abstract String[] getAvailablePortsNames();
}
