package com.clumsy.gymbadger.data;

public enum AnnouncementType {
    INFO(0x0000),
    NORMAL(0x0001),
    WARN(0x002),
    DANGER(0x003);

    private final int value;

    AnnouncementType(int value) {
        this.value = value;
    }

    public final boolean is(AnnouncementType type) {
        return type.value == this.value;
    }

    public static AnnouncementType fromStringIgnoreCase(final String string) {
        for (final AnnouncementType value : AnnouncementType.values()) {
            if (string.equalsIgnoreCase(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown value " + string);
    }

	public static AnnouncementType fromInt(int type) {
		for (final AnnouncementType value : AnnouncementType.values()) {
            if (value.value==type) {
                return value;
            }
        }
		throw new IllegalArgumentException("Unknown value " + type); 
	}
}
