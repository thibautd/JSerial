import dk.thibaut.serial.SerialException;
import dk.thibaut.serial.SerialPort;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/*
 * Because JSerial rely a lot on the native implementations, I prefer
 * to have real unit tests, with native API calls. For this to work
 * you need to have a virtual port utility installed on you system.
 *
 * I recommend the free (but limited) Free Virtual Serial Port by HDD
 * Software (http://freevirtualserialports.com/).
 *
 * Once your software is configured, configure both PORT_READ and PORT_WRITE
 * constants to virtual port names. Test will write data on PORT_WRITE
 * and read it from PORT_READ to test different functions and errors.
 */

public class TestSerialPortWindows {

    private static final String PORT_READ = "COM2";
    private static final String PORT_WRITE = "COM4";

    private SerialPort portRead;
    private SerialPort portWrite;

    @Before
    public void setUp() throws IOException {
        portRead = SerialPort.open("COM2");
        portWrite = SerialPort.open("COM4");
    }

    @After
    public void tearDown() throws IOException {
        portRead.close();
        portWrite.close();
    }

    @Test(expected = SerialException.class)
    public void testOpenFails() throws IOException {
        SerialPort.open("COM254");
    }

    @Test
    public void testReadWrite() throws IOException {
        ByteBuffer toWrite = ByteBuffer.allocateDirect(50);
        for (byte b = 0; b < toWrite.capacity(); b++)
            toWrite.put(b);
        toWrite.clear();
        portWrite.getChannel().write(toWrite);
        ByteBuffer toRead = ByteBuffer.allocateDirect(50);
        portRead.getChannel().read(toRead);
        assertEquals(toRead.get(0), 0);
        assertEquals(toRead.get(25), 25);
        assertEquals(toRead.get(49), 49);
    }

}
