package com.devblo.command;

import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteEventCommand implements Command {

    private final Event event;
    private final Map<String, Event> eventMap;


    // 👇 Store for undo
    private final List<Observer> removedObservers;

    public DeleteEventCommand(Event event, Map<String, Event> eventMap) {
        this.event = event;
        this.eventMap = eventMap;
        this.removedObservers = new ArrayList<>(event.getObservers());
    }

    @Override
    public void execute() {

        for (Observer o : removedObservers) {
            event.removeObserver(o);
            if (o instanceof User user) {
                user.removeEvent(event);
            }
        }

        eventMap.remove(event.getName());
        System.out.println("🗑️ Event deleted: " + event.getName());
    }

    @Override
    public void undo() {
        eventMap.put(event.getName(), event);

        // Reassign all previous observers
        for (Observer o : removedObservers) {
            event.addObserver(o);
            if (o instanceof User user) {
                user.addEvent(event); // Restore event reference to user
            }
        }

        event.notifyObservers();
        System.out.println("↩️ Undo: Event restored: " + event.getName());
    }
}
