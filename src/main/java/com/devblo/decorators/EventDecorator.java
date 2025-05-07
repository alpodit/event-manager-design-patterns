package com.devblo.decorators;

import com.devblo.models.Event;
import com.devblo.observer.Observer;
import com.devblo.observer.Subject;

import java.util.List;

public class EventDecorator extends Event implements Subject {
    protected Event event;

    public EventDecorator(Event event) {
        // String name, String location, LocalDateTime dateTime, String description, String organizer, User creator
        super(event.getName(), event.getLocation(), event.getDateTime(), event.getDescriptionText(), event.getOrganizer(), event.getCreatorUser());
        this.event = event;
    }

    // Delegate observer methods to the wrapped event
    @Override
    public void addObserver(Observer observer) {
        event.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        event.removeObserver(observer);
    }

    @Override
    public void notifyObservers() {
        event.notifyObservers();
    }

    @Override
    public List<Observer> getObservers() {
        return event.getObservers();
    }

    // Delegate other methods
    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getDescriptionText() {
        return event.getDescriptionText();
    }

    @Override
    public void setDescription(String description) {
        event.setDescription(description);
    }

    @Override
    public List<Tag> getTags() {
        return event.getTags();
    }

    @Override
    public List<Category> getCategories() {
        return event.getCategories();
    }
}
