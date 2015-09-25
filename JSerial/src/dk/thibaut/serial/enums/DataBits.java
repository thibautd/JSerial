package dk.thibaut.serial.enums;

public enum DataBits {

    UNKNOWN(0),
    D5(5),
    D7(7),
    D8(8);

    private int value;

    public static DataBits fromInteger(int value) {
        for (DataBits d : DataBits.values())
            if (d.value == value)
                return d;
        return UNKNOWN;
    }

    DataBits(int value) {
        this.value = value;
    }

    public int toInteger() {
        return value;
    }

    @Override
    public String toString() {
        if (this == UNKNOWN)
            return "UNKNOWN";
        return Integer.toString(value);
    }
}
