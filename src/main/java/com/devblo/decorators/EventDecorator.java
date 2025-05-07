package com.devblo.decorators;

import com.devblo.models.Event;

import java.time.LocalDateTime;
import java.util.List;

public abstract class EventDecorator extends Event {
    protected Event event;

    public EventDecorator(Event event) {
        super(event.getName(), event.getLocation(), event.getDateTime(), event.getOrganizer());
        this.event = event;
    }

    @Override
    public String getName() {
        return event.getName();
    }

    @Override
    public String getLocation() {
        return event.getLocation();
    }

    @Override
    public LocalDateTime getDateTime() {
        return event.getDateTime();
    }

    @Override
    public String getOrganizer() {
        return event.getOrganizer();
    }

    @Override
    public List<com.devblo.observer.Observer> getObservers() {
        return event.getObservers();
    }
}
