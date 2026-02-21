package com.gyanamala.vartalap;

public class Island {
    private String islandId;
    private String name;
    private String type; // "GLOBAL", "PUBLIC", or "LOCKED"
    private String pin;  // Null if public
    private long timestamp;

    // Required empty constructor for Firebase DataSnapshot mapping
    public Island() {
    }

    public Island(String islandId, String name, String type, String pin, long timestamp) {
        this.islandId = islandId;
        this.name = name;
        this.type = type;
        this.pin = pin;
        this.timestamp = timestamp;
    }

    public String getIslandId() { return islandId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getPin() { return pin; }
    public long getTimestamp() { return timestamp; }
}

