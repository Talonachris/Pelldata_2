package com.talona.pellData;

public class StatEntry {

    private final String uuid;
    private final int value;

    public StatEntry(String uuid, int value) {
        this.uuid = uuid;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public int getValue() {
        return value;
    }
}
