package com.synapseevent.utils;

public class EventContext {
    private static Long selectedEventId;
    private static Long selectedCourtId;
    private static Boolean showBrowseTab = false;
    private static String previousPage = null; // To track where to go back
    
    public static void setSelectedEventId(Long eventId) {
        selectedEventId = eventId;
    }
    
    public static Long getSelectedEventId() {
        return selectedEventId;
    }
    
    public static void setSelectedCourtId(Long courtId) {
        selectedCourtId = courtId;
    }
    
    public static Long getSelectedCourtId() {
        return selectedCourtId;
    }
    
    public static void setShowBrowseTab(Boolean show) {
        showBrowseTab = show;
    }
    
    public static Boolean getShowBrowseTab() {
        return showBrowseTab;
    }
    
    public static void setPreviousPage(String page) {
        previousPage = page;
    }
    
    public static String getPreviousPage() {
        return previousPage;
    }
    
    public static void clear() {
        selectedEventId = null;
        selectedCourtId = null;
        showBrowseTab = false;
        previousPage = null;
    }
}
