package com.devblo;

import com.devblo.command.UpdateEventCommand;
import com.devblo.decorators.Category;
import com.devblo.decorators.CategoryDecorator;
import com.devblo.decorators.Tag;
import com.devblo.decorators.TagDecorator;
import com.devblo.factory.EventFactory;
import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EventManagerTest {

    private Map<String, Event> eventMap;
    private User alice;
    private User bob;
    private Event baseEvent;

    @BeforeEach
    void setUp() {
        eventMap = new HashMap<>();
        alice = new User("alice", "pass");
        bob = new User("bob", "pass");

        baseEvent = EventFactory.createEvent(
                "Tech Talk",
                "Zoom",
                LocalDateTime.of(2025, 5, 6, 12, 0),
                "Talk about tech",
                "Alice",
                alice
        );

        // Wrap in decorators
        TagDecorator tagWrapped = new TagDecorator(baseEvent);
        tagWrapped.addTag(Tag.ONLINE);
        tagWrapped.addTag(Tag.FREE_ENTRY);

        CategoryDecorator fullEvent = new CategoryDecorator(tagWrapped);
        fullEvent.addCategory(Category.SEMINAR);

        eventMap.put(fullEvent.getName(), fullEvent);
    }

    @Test
    void testUserAndEventCreation() {
        assertNotNull(alice);
        assertNotNull(bob);
        assertTrue(eventMap.containsKey("Tech Talk"));
    }

    @Test
    void testRegistrationAndUnregistration() {
        Event e = eventMap.get("Tech Talk");
        e.addObserver(bob);
        bob.addEvent(e);
        assertTrue(e.getObservers().contains(bob));
        assertTrue(bob.getRegisteredEvents().contains(e));

        e.removeObserver(bob);
        bob.removeEvent(e);
        assertFalse(e.getObservers().contains(bob));
        assertFalse(bob.getRegisteredEvents().contains(e));
    }

    @Test
    void testEventUpdateAndUndo() throws Exception {
        Event original = eventMap.get("Tech Talk");
        System.out.println("Actual class of event in map: original " + original.getClass().getName());
        System.out.println(original.getTags());
        System.out.println(original.getCategories());

        UpdateEventCommand cmd = new UpdateEventCommand(
                original,
                original.getName(),
                "New Venue",
                "Updated Description",
                LocalDateTime.of(2025, 6, 1, 14, 30),
                List.of(Tag.FAMILY_FRIENDLY),
                List.of(Category.WORKSHOP),
                eventMap,
                alice
        );

        cmd.execute();
        Event updated = eventMap.get("Tech Talk");
        System.out.println("Actual class of event in map: " + original.getClass().getName());
        List<Tag> tags = updated.getTags();  // SHOULD call overridden version

        System.out.println("Tags: " + tags);

        assertEquals("New Venue", updated.getLocation());
        assertEquals("Updated Description", updated.getDescriptionText());
        System.out.println(updated.getTags());
        System.out.println(updated.getCategories());
        assertTrue(updated.getTags().contains(Tag.FAMILY_FRIENDLY));
        assertTrue(updated.getCategories().contains(Category.WORKSHOP));


        cmd.undo();

        Event reverted = eventMap.get("Tech Talk");
        System.out.println("Reverted Tags: " + reverted.getTags());  // Should include FREE_ENTRY
        System.out.println("Reverted Class: " + reverted.getClass().getSimpleName());  // Should be CategoryDecorator
        assertEquals("Zoom", reverted.getLocation());
        assertEquals("Talk about tech", reverted.getDescriptionText());
        assertTrue(reverted.getTags().contains(Tag.FREE_ENTRY));
    }

    @Test
    void testDecoratorTagLimitAndDuplication() {
        TagDecorator td = new TagDecorator(baseEvent);
        td.addTag(Tag.ONLINE);
        td.addTag(Tag.FREE_ENTRY);
        td.addTag(Tag.FAMILY_FRIENDLY);
        td.addTag(Tag.FREE_ENTRY); // duplicate

        List<Tag> tags = td.getTags();
        assertEquals(3, tags.size());
        assertTrue(tags.contains(Tag.ONLINE));
    }

    @Test
    void testObserverNotification() {
        StringBuilder result = new StringBuilder();
        Observer mock = event -> result.append("📢");
        baseEvent.addObserver(mock);
        baseEvent.notifyObservers();
        assertTrue(result.toString().contains("📢"));
    }

    @Test
    void testObserversDoNotDuplicateAfterUpdate() throws Exception {
        Event original = eventMap.get("Tech Talk");
        original.addObserver(bob);
        bob.addEvent(original);


        UpdateEventCommand cmd = new UpdateEventCommand(
                original,
                original.getName(),
                "Hall A",
                "New content",
                LocalDateTime.of(2025, 6, 10, 9, 0),
                List.of(Tag.ONLINE),
                List.of(Category.SEMINAR),
                eventMap,
                alice
        );

        cmd.execute();
        Event updated = eventMap.get("Tech Talk");
        long count = updated.getObservers().stream().filter(o -> o == bob).count();
        assertEquals(1, count);
    }
}
