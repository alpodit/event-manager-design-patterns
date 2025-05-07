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
    private final String eventKey;
    private final Event oldEvent;
    private final Event newEvent;
    private final Map<String, Event> eventMap;

    private final List<Observer> previousObservers; // to restore in undo

    public UpdateEventCommand(Event original,
                              String newLocation,
                              LocalDateTime newDateTime,
                              List<Tag> newTags,
                              List<Category> newCategories,
                              Map<String, Event> eventMap,
                              User currentUser) {

        this.eventKey = original.getName();
        this.oldEvent = original;
        this.eventMap = eventMap;

        // ✅ Make a shallow copy of observers to restore on undo
        this.previousObservers = List.copyOf(original.getObservers());

        // ✅ Create updated base event using factory
        Event updatedCore = EventFactory.createEvent(
                original.getName(),
                newLocation,
                newDateTime,
                original.getOrganizer(),
                currentUser
        );
        updatedCore.setCreatorUsername(original.getCreatorUsername());

        // ✅ Decorate
        TagDecorator tagWrapped = new TagDecorator(updatedCore);
        newTags.forEach(tagWrapped::addTag);

        CategoryDecorator fullyDecorated = new CategoryDecorator(tagWrapped);
        newCategories.forEach(fullyDecorated::addCategory);

        this.newEvent = fullyDecorated;

        // ✅ Reassign all observers to new decorated event
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
        eventMap.put(eventKey, newEvent);
        newEvent.notifyObservers();
        System.out.println("✅ Event updated.");
    }

    @Override
    public void undo() {
        // Remove new event from all observers
        for (Observer o : previousObservers) {
            newEvent.removeObserver(o);
            if (o instanceof User user) {
                user.removeEvent(newEvent);
                user.addEvent(oldEvent); // restore original
            }
            oldEvent.addObserver(o); // restore observer list
        }

        eventMap.put(eventKey, oldEvent);
        oldEvent.notifyObservers();
        System.out.println("↩️ Undo: Reverted event to original version.");
    }
}
