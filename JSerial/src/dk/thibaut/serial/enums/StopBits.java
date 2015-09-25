package dk.thibaut.serial.enums;

public enum StopBits {

    UNKNOWN("Unknown"),
    ONE("1"),
    ONE_HALF("1.5"),
    TWO("2");

    private String value;

    StopBits(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
