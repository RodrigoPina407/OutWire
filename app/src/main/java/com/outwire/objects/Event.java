package com.outwire.objects;

import android.location.Location;

import java.util.Date;

public class Event {

    public static final boolean PUBLIC = true;
    public static final boolean PRIVATE = false;
    public static final boolean ALLOW_MEDIA_SHARE = true;
    public static final boolean DONT_ALLOW_MEDIA_SHARE = false;


    private String eventCreator;
    private String eventCategory;
    private boolean allowMediaShare;
    private boolean eventVisibility;
    private Date date;
    private String eventName;
    private String eventDescription;
    private Location eventLocation;
    private int numParticipants;
    private String price;

    public Event() {

    }

    public void setEventCreator(String eventCreator){
        this.eventCreator = eventCreator;
    }
    public String getEventCreator() {
        return eventCreator;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Location getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Location eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public boolean isAllowMediaShare() {
        return allowMediaShare;
    }

    public void setAllowMediaShare(boolean allowMediaShare) {
        this.allowMediaShare = allowMediaShare;
    }

    public boolean isEventVisibility() {
        return eventVisibility;
    }

    public void setEventVisibility(boolean eventVisibility) {
        this.eventVisibility = eventVisibility;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
