package dk.thibaut.serial.enums;

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
