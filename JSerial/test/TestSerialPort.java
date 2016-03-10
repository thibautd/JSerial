/*
 * Copyright (c) 2015 Thibaut DIRLIK <thibaut.dirlik@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import dk.thibaut.serial.SerialConfig;
import dk.thibaut.serial.SerialException;
import dk.thibaut.serial.SerialPort;

import dk.thibaut.serial.BaudRate;
import dk.thibaut.serial.DataBits;
import dk.thibaut.serial.Parity;
import dk.thibaut.serial.StopBits;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

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
 *
 * For Linux, I run unit test in Virtual Box with my virtual windows ports
 * mapped to /dev/ttyS0 and /dev/ttyS1. It's a complex setup, but at least
 * we can test real system calls.
 */

public class TestSerialPort {

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            PORT_READ = "COM2";
            PORT_WRITE = "COM4";
        } else if (os.contains("linux")) {
            PORT_READ = "/dev/ttyS0";
            PORT_WRITE = "/dev/ttyS1";
        }
    }

    private static String PORT_READ;
    private static String PORT_WRITE;

    private static final SerialConfig DEFAULT_CONFIG = new SerialConfig(
        BaudRate.B115200, Parity.NONE, StopBits.ONE, DataBits.D8);

    private SerialPort portRead;
    private SerialPort portWrite;

    @Before
    public void setUp() throws IOException {
        portRead = SerialPort.open(PORT_READ);
        portRead.setConfig(DEFAULT_CONFIG);
        portRead.setTimeout(SerialPort.TIMEOUT_INFINITE);
        portWrite = SerialPort.open(PORT_WRITE);
        portWrite.setConfig(DEFAULT_CONFIG);
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
    public void testGetSetConfig() throws IOException {
        /* Sets some strange configuration on the port. */
        SerialConfig config = new SerialConfig(BaudRate.B256000,
            Parity.EVEN, StopBits.ONE_HALF, DataBits.D7);
        portRead.setConfig(config);
        portRead.close();
        /* Re-opens the port, and check that this is the current
         * configuration, which means it was applied correctly. */
        portRead = SerialPort.open(PORT_READ);
        config = portRead.getConfig();
        assertEquals(config.BaudRate, BaudRate.B256000);
        assertEquals(config.Parity, Parity.EVEN);
        assertEquals(config.StopBits, StopBits.ONE_HALF);
        assertEquals(config.DataBits, DataBits.D7);
    }

    @Test
    public void testReadWrite() throws IOException {
        ByteBuffer toWrite = ByteBuffer.allocateDirect(50);
        for (byte b = 0; b < toWrite.capacity(); b++)
            toWrite.put(b);
        toWrite.clear();
        portWrite.getChannel().write(toWrite);
        assertEquals(toWrite.remaining(), 0);
        ByteBuffer toRead = ByteBuffer.allocateDirect(50);
        portRead.getChannel().read(toRead);
        assertEquals(toRead.position(), 50);
        assertEquals(toRead.get(0), 0);
        assertEquals(toRead.get(25), 25);
        assertEquals(toRead.get(49), 49);
    }

    @Test
    public void testReadWriteStream() throws IOException {
        InputStream istream = portRead.getInputStream();
        OutputStream ostream = portWrite.getOutputStream();
        byte[] data = new byte[50];
        for (int i = 0; i < data.length; i++)
            data[i] = (byte)i;
        ostream.write(data);
        assertEquals(istream.read(), 0);
        assertEquals(istream.read(), 1);
        assertEquals(istream.read(), 2);
    }

    @Test
    public void testTimeoutValue() throws IOException, InterruptedException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(5);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    portRead.getChannel().read(buffer);
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });
        portRead.setTimeout(110);
        thread.start();
        thread.join(100);
        assertTrue(thread.isAlive());
        thread.join(20);
        assertFalse(thread.isAlive());
    }

    @Test
    public void testGetPorts() {
        List<String> portsNames = SerialPort.getAvailablePortsNames();
        assertTrue(portsNames.contains(PORT_READ));
        assertTrue(portsNames.contains(PORT_WRITE));
    }

    @Test
    public void testSetRts() throws IOException, InterruptedException {
        /* We add little sleep because setting the line up is not blocking
         * and so it could fail if the system is slow. */
        portRead.setRts(true);
        Thread.sleep(5);
        assertEquals(portWrite.getCts(), true);
        portRead.setRts(false);
        Thread.sleep(5);
        assertEquals(portWrite.getCts(), false);
        portWrite.setRts(true);
        Thread.sleep(5);
        assertEquals(portRead.getCts(), true);
        portWrite.setRts(false);
        Thread.sleep(5);
        assertEquals(portRead.getCts(), false);
    }

    @Test
    public void testSetDtr() throws IOException, InterruptedException {
        /* We add little sleep because setting the line up is not blocking
         * and so it could fail if the system is slow. */
        portRead.setDtr(true);
        Thread.sleep(5);
        assertEquals(portWrite.getDsr(), true);
        portRead.setDtr(false);
        Thread.sleep(5);
        assertEquals(portWrite.getDsr(), false);
        portWrite.setDtr(true);
        Thread.sleep(5);
        assertEquals(portRead.getDsr(), true);
        portWrite.setDtr(false);
        Thread.sleep(5);
        assertEquals(portRead.getDsr(), false);
    }

    @Test
    public void testFlush() throws IOException {
        /* Nothing easy to test, just test the call. */
        portRead.getChannel().flush(true, true);
    }

    @Test
    public void testName() {
        assertEquals(portRead.getName(), PORT_READ);
        assertEquals(portWrite.getName(), PORT_WRITE);
    }

    @Test
    public void testTimeout() throws IOException {
        portRead.setTimeout(SerialPort.TIMEOUT_IMMEDIATE);
        assertEquals(portRead.getTimeout(), SerialPort.TIMEOUT_IMMEDIATE);
        portRead.setTimeout(SerialPort.TIMEOUT_INFINITE);
        assertEquals(portRead.getTimeout(), SerialPort.TIMEOUT_INFINITE);
        portRead.setTimeout(1337);
        assertEquals(portRead.getTimeout(), 1337);
    }

}
