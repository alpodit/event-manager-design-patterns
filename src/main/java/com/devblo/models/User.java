package com.devblo.models;

import java.util.ArrayList;
import java.util.List;

import com.devblo.observer.Observer;

public class User implements Observer {
    private String username;
    private List<Event> registeredEvents = new ArrayList<>();
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean verifyPassword(String input) {
        return this.password.equals(input);
    }


    public String getUsername() {
        return username;
    }

    public void addEvent(Event event) {
        registeredEvents.add(event);
    }

    public void removeEvent(Event event) {
        registeredEvents.remove(event);
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    @Override
    public void update(Event event) {
        System.out.println("User '" + username + "' was notified: Event '" + event.getName() + "' has been updated.");
    }
}
