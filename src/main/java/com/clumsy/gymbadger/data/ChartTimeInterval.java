package com.clumsy.gymbadger.data;

public enum ChartTimeInterval {
    DAY(0x0000),
    WEEK(0x0001),
    MONTH(0x002),
    QUARTER(0x003),
    YEAR(0x004),
    ALL(0x005),
    CUSTOM(0x006);

    private final int value;

    ChartTimeInterval(int value) {
        this.value = value;
    }

    public final boolean is(ChartTimeInterval type) {
        return type.value == this.value;
    }

    public static ChartTimeInterval fromStringIgnoreCase(final String string) {
        for (final ChartTimeInterval value : ChartTimeInterval.values()) {
            if (string.equalsIgnoreCase(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown value " + string);
    }
}
