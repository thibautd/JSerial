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

package dk.thibaut.serial;

/**
 * This class represent the configuration of a serial port.
 * <p>
 * It's use by the {@link SerialPort#setConfig(SerialConfig)} function
 * to set the configuration (baudrate, parity, stopbits and databits)
 * of the srial connection.
 * <p>
 * You can also get an instance of this object by calling the
 * {@link SerialPort#getConfig()} method to access the current
 * port configuration.
 */
public class SerialConfig {

    public BaudRate BaudRate;
    public Parity Parity;
    public StopBits StopBits;
    public DataBits DataBits;

    public SerialConfig(BaudRate b, Parity p, StopBits s, DataBits d) {
        BaudRate = b;
        Parity = p;
        StopBits = s;
        DataBits = d;
    }

    @Override
    public String toString() {
        return String.format("SerialConfig(BaudRate=%s, Parity=%s, StopBits=%s, DataBits=%s",
            BaudRate, Parity, StopBits, DataBits);
    }

}
