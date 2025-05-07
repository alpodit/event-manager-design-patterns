package com.devblo.factory;

import com.devblo.models.Event;
import com.devblo.models.User;

import java.time.LocalDateTime;

public class EventFactory {
    public static Event createEvent(String name, String location, LocalDateTime dateTime, String description, String organizer, User creator) {
        return new Event(name, location, dateTime, description, organizer, creator);
    }
}