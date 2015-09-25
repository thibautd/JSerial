package dk.thibaut.serial;

import dk.thibaut.serial.enums.BaudRate;
import dk.thibaut.serial.enums.DataBits;
import dk.thibaut.serial.enums.Parity;
import dk.thibaut.serial.enums.StopBits;

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
