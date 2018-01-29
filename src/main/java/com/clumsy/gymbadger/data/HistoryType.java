package com.clumsy.gymbadger.data;

public enum HistoryType {
    BADGE(0x0000),
    RAID(0x0001);

    private final int value;

    HistoryType(int value) {
        this.value = value;
    }

    public final boolean is(HistoryType type) {
        return type.value == this.value;
    }

    public static HistoryType fromStringIgnoreCase(final String string) {
        for (final HistoryType value : HistoryType.values()) {
            if (string.equalsIgnoreCase(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown value " + string);
    }
}