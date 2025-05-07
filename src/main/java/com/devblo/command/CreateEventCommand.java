package com.devblo.command;

import com.devblo.models.Event;

import java.util.Map;

public class CreateEventCommand implements Command {
    private Event event;
    private Map<String, Event> eventMap;

    public CreateEventCommand(Event event, Map<String, Event> eventMap) {
        this.event = event;
        this.eventMap = eventMap;
    }

    @Override
    public void execute() {
        eventMap.put(event.getName(), event);
        System.out.println("✅ Event saved: " + event.getName());
    }

    @Override
    public void undo() {
        eventMap.remove(event.getName());
        System.out.println("↩️ Event creation undone: " + event.getName());
    }
}
