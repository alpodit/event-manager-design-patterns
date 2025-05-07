package com.devblo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.devblo.observer.Subject;
import com.devblo.observer.Observer;

public class Event implements Subject {
    private String name;
    private String location;
    private LocalDateTime dateTime;
    private String organizer;

    private List<Observer> observers = new ArrayList<>();

    public Event(String name, String location, LocalDateTime dateTime, String organizer) {
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.organizer = organizer;
    }

    private String creatorUsername;

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }


    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getOrganizer() {
        return organizer;
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(this);
        }
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public String getDescription() {
        return "Event: " + name + ", Location: " + location + ", Date: " + dateTime + ", Organizer: " + organizer;
    }

    public List<Observer> copyObservers() {
        return new ArrayList<>(observers); // safe shallow copy
    }

}
