package com.devblo.command;

import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.observer.Observer;
import com.devblo.ui.ConsoleUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteEventCommand implements Command {
    private final Event event;
    private final Map<String, Event> eventMap;
    private final User user;

    private final List<Observer> previousObservers;



    public DeleteEventCommand(Event event, User user, Map<String, Event> eventMap) {
        this.event = event;
        this.user = user;
        this.eventMap=eventMap;
        previousObservers = new ArrayList<>(event.getObservers());
    }


    @Override
    public void execute() {
        List<Observer> removedObservers = new ArrayList<>(event.getObservers());
        for (Observer o : removedObservers) {
            event.removeObserver(o);
            if (o instanceof User user) {
                user.removeEvent(event);
            }
        }

        eventMap.remove(event.getName());
        user.pushCommand(this);
        System.out.println("🗑️ Event deleted: " + event.getName());
    }

    @Override
    public void undo() {
        for (Observer observer : previousObservers) {
            event.addObserver(observer);
            if (observer instanceof User user) {
                user.addEvent(event);
            }
        }

        eventMap.put(event.getName(), event);
        event.notifyObservers();
        ConsoleUI.print("↩️ Undo: Restored deleted event: " + event.getName());
    }
}
