package com.synapseevent.utils;

public class EventContext {
    private static Long selectedEventId;
    
    public static void setSelectedEventId(Long eventId) {
        selectedEventId = eventId;
    }
    
    public static Long getSelectedEventId() {
        return selectedEventId;
    }
    
    public static void clear() {
        selectedEventId = null;
    }
}
