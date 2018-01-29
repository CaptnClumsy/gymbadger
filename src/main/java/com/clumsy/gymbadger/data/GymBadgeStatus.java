package com.clumsy.gymbadger.data;

public enum GymBadgeStatus {
    NONE(0x0000),
    BASIC(0x0001),
    BRONZE(0x002),
    SILVER(0x003),
    GOLD(0x004);

    private final int value;

    GymBadgeStatus(int value) {
        this.value = value;
    }

    public final boolean is(GymBadgeStatus type) {
        return type.value == this.value;
    }

    public static GymBadgeStatus fromStringIgnoreCase(final String string) {
        for (final GymBadgeStatus value : GymBadgeStatus.values()) {
            if (string.equalsIgnoreCase(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown value " + string);
    }
}