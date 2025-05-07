package com.devblo.command;

import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.decorators.Tag;
import com.devblo.decorators.Category;
import com.devblo.decorators.TagDecorator;
import com.devblo.decorators.CategoryDecorator;
import com.devblo.factory.EventFactory;
import com.devblo.observer.Observer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UpdateEventCommand implements Command {
    private final String newName;
    private final String oldName;
    private final Event oldEvent;
    private final Event newEvent;
    private final Map<String, Event> eventMap;
    private final List<Observer> previousObservers;

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
        this.previousObservers = List.copyOf(original.getObservers());

        // ✅ Use factory with all updated fields
        Event updatedCore = EventFactory.createEvent(
                updatedName,
                updatedLocation,
                updatedDateTime,
                original.getOrganizer(),
                updatedDescription,
                currentUser
        );

        updatedCore.setDescription(updatedDescription);

        // ✅ Decorate
        TagDecorator tagWrapped = new TagDecorator(updatedCore);
        newTags.forEach(tagWrapped::addTag);

        CategoryDecorator fullyDecorated = new CategoryDecorator(tagWrapped);
        newCategories.forEach(fullyDecorated::addCategory);

        this.newEvent = fullyDecorated;

        // ✅ Transfer observers
        for (Observer o : previousObservers) {
            newEvent.addObserver(o);
            if (o instanceof User user) {
                user.removeEvent(oldEvent);
                user.addEvent(newEvent);
            }
        }
    }

    @Override
    public void execute() {
        // Remove old entry if name changed
        if (!newName.equals(oldName)) {
            eventMap.remove(oldName);
        }

        eventMap.put(newName, newEvent);
        newEvent.notifyObservers();
        System.out.println("✅ Event updated.");
    }

    @Override
    public void undo() {
        // Unlink newEvent from users and restore oldEvent
        for (Observer o : previousObservers) {
            newEvent.removeObserver(o);
            if (o instanceof User user) {
                user.removeEvent(newEvent);
                user.addEvent(oldEvent);
            }
            oldEvent.addObserver(o);
        }

        eventMap.remove(newName);
        eventMap.put(oldName, oldEvent);
        oldEvent.notifyObservers();
        System.out.println("↩️ Undo: Reverted event to original version.");
    }
}
