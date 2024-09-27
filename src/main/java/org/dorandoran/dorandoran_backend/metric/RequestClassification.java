package org.dorandoran.dorandoran_backend.metric;

public enum RequestClassification {
    BOOK("Book"),
    USER("User"),
    DEBATE_ROOM("DebateRoom"),
    DEBATE_ROOM_USER("DebateRoomUser"),
    AI("AI");

    private final String label;

    RequestClassification(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
