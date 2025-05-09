package com.devblo.command;

import com.devblo.decorators.CategoryDecorator;
import com.devblo.decorators.TagDecorator;
import com.devblo.factory.EventFactory;
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
    public void undo() throws Exception {
        boolean renamed = false;
        if (eventMap.containsKey(event.getName())) {
            ConsoleUI.print("Same event name exists.");
            ConsoleUI.print("");
            String name = ConsoleUI.prompt("Enter new distinct event name (blank to cancel):").trim();
            while (true){

                if (name.isEmpty()) {
                    throw new Exception("Same event name exists.");
                }

                if (eventMap.containsKey(name)) {
                    ConsoleUI.print("<UNK> Event name already exists.");

                    name = ConsoleUI.prompt("Enter new distinct event name (blank to cancel):").trim();
                }else {
                    renamed = true;
                    break;
                }
            }

        }

        // should create a new event to fix renaming
        if (renamed) {
            Event baseEvent = EventFactory.createEvent(event.getName(), event.getLocation(), event.getDateTime(), event.getDescription(), event.getOrganizer(), event.getCreatorUser());

            // Decorate with tags
            TagDecorator taggedEvent = new TagDecorator(baseEvent);
            event.getTags().forEach(tag -> {
                taggedEvent.addTag(tag);
            });

            // Decorate with categories
            CategoryDecorator categorizedEvent = new CategoryDecorator(taggedEvent);
            event.getCategories().forEach(category -> {categorizedEvent.addCategory(category);});

            for (Observer observer : previousObservers) {
                categorizedEvent.addObserver(observer);
                if (observer instanceof User user) {
                    user.addEvent(categorizedEvent);
                }
            }

            eventMap.put(categorizedEvent.getName(), categorizedEvent);
            ConsoleUI.print("↩️ Undo: Restored deleted event: " + event.getName());
        }else{
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
}
