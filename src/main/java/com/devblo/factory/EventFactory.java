package com.devblo.factory;

import java.time.LocalDateTime;

import com.devblo.models.Event;
import com.devblo.models.User;


public class EventFactory {

    public static Event createEvent(String name, String location, LocalDateTime dateTime, String organizer, User currentUser) {
        Event e = new Event(name, location, dateTime, organizer);
        e.addObserver(currentUser);
        currentUser.addEvent(e);

        return e;
    }
}
