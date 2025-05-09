package com.devblo.command;

import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.decorators.Tag;
import com.devblo.decorators.Category;
import com.devblo.decorators.TagDecorator;
import com.devblo.decorators.CategoryDecorator;
import com.devblo.factory.EventFactory;
import com.devblo.observer.Observer;
import com.devblo.ui.ConsoleUI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateEventCommand implements Command {
    private final String newName;
    private final String oldName;
    private final Event oldEvent;
    private Event newEvent;
    private final Map<String, Event> eventMap;
    private final List<Observer> previousObservers;
    private final User user;

    public UpdateEventCommand(Event original,
                              String updatedName,
                              String updatedLocation,
                              String updatedDescription,
                              LocalDateTime updatedDateTime,
                              List<Tag> newTags,
                              List<Category> newCategories,
                              Map<String, Event> eventMap,
                              User currentUser) {

        this.oldName = original.getName();
        this.newName = updatedName;
        this.oldEvent = original;
        this.eventMap = eventMap;
        this.previousObservers = new ArrayList<>(original.getObservers());
        this.user=currentUser;

        // ✅ Use factory with all updated fields
        Event updatedCore = EventFactory.createEvent(
                updatedName,
                updatedLocation,
                updatedDateTime,
                updatedDescription,
                original.getOrganizer(),
                currentUser
        );

        // ✅ Decorate
        TagDecorator tagWrapped = new TagDecorator(updatedCore);
        newTags.forEach(tagWrapped::addTag);

        CategoryDecorator fullyDecorated = new CategoryDecorator(tagWrapped);
        newCategories.forEach(fullyDecorated::addCategory);

        this.newEvent = fullyDecorated;
    }

    @Override
    public void execute() {
        // Remove old entry if name changed
        if (!newName.equals(oldName)) {
            eventMap.remove(oldName);
        }

        // Clear old observers to avoid duplicates
        for (Observer observer : previousObservers) {
            oldEvent.removeObserver(observer);
            if (observer instanceof User user) {
                user.removeEvent(oldEvent);
            }
        }

        // Add observers back to new event and update users
        for (Observer observer : previousObservers) {
            newEvent.addObserver(observer);
            if (observer instanceof User user) {
                user.addEvent(newEvent);
            }
        }

        // Update map and notify observers
        eventMap.put(newName, newEvent);
        newEvent.notifyObservers();
        user.pushCommand(this);
        System.out.println("✅ Event updated.");
    }

    @Override
    public void undo() {
        // First, clear observers from the new event
        List<Observer> currentObservers = new ArrayList<>(newEvent.getObservers());
        for (Observer observer : currentObservers) {
            newEvent.removeObserver(observer);
            if (observer instanceof User user) {
                user.removeEvent(newEvent);
            }
        }

        // Restore observers to the old event
        for (Observer observer : previousObservers) {
            oldEvent.addObserver(observer);
            if (observer instanceof User user) {
                user.addEvent(oldEvent);
            }
        }

        // Update map
        eventMap.remove(newName);
        eventMap.put(oldName, oldEvent);

        // Notify observers
        oldEvent.notifyObservers();
        ConsoleUI.print("↩️ Undo: Reverted event: "+oldName+" to original version.");
    }
}