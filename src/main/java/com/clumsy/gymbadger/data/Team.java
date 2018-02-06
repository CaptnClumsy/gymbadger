package com.clumsy.gymbadger.data;

public enum Team {
    NONE(0x0000),
    MYSTIC(0x0001),
    VALOR(0x002),
    INSTINCT(0x003);

    private final int value;

    Team(int value) {
        this.value = value;
    }

    public final boolean is(Team type) {
        return type.value == this.value;
    }

    public static Team fromStringIgnoreCase(final String string) {
        for (final Team value : Team.values()) {
            if (string.equalsIgnoreCase(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown value " + string);
    }
}