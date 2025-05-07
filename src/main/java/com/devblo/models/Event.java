package com.devblo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.devblo.decorators.Category;
import com.devblo.decorators.Tag;
import com.devblo.observer.Subject;
import com.devblo.observer.Observer;

public class Event implements Subject {
    private String name;
    private String location;
    private LocalDateTime dateTime;
    private String organizer;
    private String description;
    private User creatorUser;

    private List<Observer> observers = new ArrayList<>();

    public Event(String name, String location, LocalDateTime dateTime, String description, String organizer,User creator) {
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.organizer = organizer;
        this.description = description;
        this.creatorUser = creator;
    }

    public User getCreatorUser() {
        return creatorUser;
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

    public String getDescriptionText() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tag> getTags() {
        return new ArrayList<>();
    }

    public List<Category> getCategories() {
        return new ArrayList<>();
    }

}
