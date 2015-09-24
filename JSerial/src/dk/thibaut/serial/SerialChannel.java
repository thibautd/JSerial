package dk.thibaut.serial;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public interface SerialChannel extends ByteChannel {
    void flush(boolean in, boolean out) throws IOException;
}
