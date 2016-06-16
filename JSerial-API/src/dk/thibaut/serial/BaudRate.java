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

public enum BaudRate {

    UNKNOWN(0),
    B256000(256000),
    B115200(115200),
    B57600(57600),
    B38400(38400),
    B19200(19200),
    B9600(9600);

    private int value;

    public static BaudRate fromInteger(int value) {
        for (BaudRate b : BaudRate.values())
            if (b.value == value)
                return b;
        return UNKNOWN;
    }

    BaudRate(int value) {
        this.value = value;
    }

    public int toInteger() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
