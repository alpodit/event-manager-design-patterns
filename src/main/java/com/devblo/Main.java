package com.devblo;

import com.devblo.command.*;
import com.devblo.decorators.Category;
import com.devblo.decorators.CategoryDecorator;
import com.devblo.decorators.Tag;
import com.devblo.decorators.TagDecorator;
import com.devblo.factory.EventFactory;
import com.devblo.models.Event;
import com.devblo.models.User;
import com.devblo.strategy.*;
import com.devblo.ui.ConsoleUI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: event silindiğinde registered user'ların list'inden çıkartılmalılar

public class Main {
    private static final Map<String, User> userMap = new HashMap<>();
    private static final Map<String, Event> eventMap = new HashMap<>();
    private static final CommandHistory commandHistory = new CommandHistory();


    private static User currentUser = null;

    public static void main(String[] args) {
        authMenu(); // Ensure login

        while (true) {
            ConsoleUI.print("\nMain Menu:");
            ConsoleUI.print("1 - Create Event");
            ConsoleUI.print("2 - Browse & Register for Events");
            ConsoleUI.print("3 - Search Events");
            ConsoleUI.print("4 - Modify an Event");
            ConsoleUI.print("5 - Undo Last Action");
            ConsoleUI.print("6 - Unregister from Event");
            ConsoleUI.print("7 - Logout");
            ConsoleUI.print("8 - Exit");


            int choice = ConsoleUI.promptInt("Select an option:");

            switch (choice) {
                case 1 -> createEventFlow();
                case 2 -> browseAndRegisterForEvents();
                case 3 -> searchEventsFlow();
                case 4 -> modifyEventFlow();
                case 5 -> commandHistory.undoLast();
                case 6 -> unregisterEventFlow();
                case 7 -> {
                    currentUser = null;
                    authMenu();
                }
                case 8 -> {
                    ConsoleUI.print("👋 Exiting...");
                    return;
                }
                default -> ConsoleUI.print("❌ Invalid choice. Try again.");
            }

        }
    }

    private static void searchEventsFlow() {
        ConsoleUI.print("\n🔍 Search Events By:");
        ConsoleUI.print("1 - Name");
        ConsoleUI.print("2 - Tag");
        ConsoleUI.print("3 - Category");
        ConsoleUI.print("4 - Date (YYYY-MM-DD)");
        ConsoleUI.print("5 - Show All");
        ConsoleUI.print("6 - Cancel");

        int choice = ConsoleUI.promptInt("Your choice:");

        if (choice == 6) return;

        SearchStrategy strategy = switch (choice) {
            case 1 -> new SearchByName();
            case 2 -> new SearchByTag();
            case 3 -> new SearchByCategory();
            case 4 -> new SearchByDate();
            case 5 -> new ShowAllStrategy();
            default -> null;
        };

        if (strategy == null) return;

        String filter = (choice == 5) ? "" : ConsoleUI.prompt("Enter search keyword:");

        List<Event> result = strategy.search(new ArrayList<>(eventMap.values()), filter);

        if (result.isEmpty()) {
            ConsoleUI.print("🚫 No matching events found.");
            return;
        }

        ConsoleUI.print("\n🎯 Search Results:");
        for (Event e : result) {
            ConsoleUI.print("- " + e.getDescription());
        }
    }


    private static void createEventFlow() {
        ConsoleUI.print("\n📅 Create a New Event");

        String name = ConsoleUI.prompt("Enter event name:");
        String location = ConsoleUI.prompt("Enter event location:");
        LocalDateTime dateTime = promptDateTime();
        String organizer = ConsoleUI.prompt("Organizer name:");

        Event baseEvent = EventFactory.createEvent(name, location, dateTime, organizer,currentUser);
        baseEvent.setCreatorUsername(currentUser.getUsername());

        List<Tag> newTags = promptTags();
        List<Category> newCats = promptCategories();


        // Decorate with tags
        TagDecorator taggedEvent = new TagDecorator(baseEvent);
        selectTags(taggedEvent);

        // Decorate with categories
        CategoryDecorator categorizedEvent = new CategoryDecorator(taggedEvent);
        selectCategories(categorizedEvent);

        ConsoleUI.print("\n✅ Event created successfully!");
        ConsoleUI.print(categorizedEvent.getDescription());

        CreateEventCommand cmd = new CreateEventCommand(categorizedEvent, eventMap);
        cmd.execute();
        commandHistory.push(cmd);
        ConsoleUI.print("📌 Event saved: " + categorizedEvent.getName());

    }


    private static void unregisterEventFlow() {
        List<Event> userEvents = currentUser.getRegisteredEvents();

        if (userEvents.isEmpty()) {
            ConsoleUI.print("⚠️ You are not registered to any events.");
            return;
        }

        ConsoleUI.print("\n🚪 Events you're registered to:");
        for (int i = 0; i < userEvents.size(); i++) {
            ConsoleUI.print((i + 1) + " - " + userEvents.get(i).getDescription());
        }

        int index = ConsoleUI.promptInt("Select an event to unregister (0 to cancel):") - 1;

        if (index == -1) return;

        if (index < 0 || index >= userEvents.size()) {
            ConsoleUI.print("❌ Invalid selection.");
            return;
        }

        Event event = userEvents.get(index);
        event.removeObserver(currentUser);
        currentUser.removeEvent(event);

        ConsoleUI.print("✅ You have unregistered from: " + event.getName());
    }

    private static void modifyEventFlow() {
        List<Event> owned = currentUser.getRegisteredEvents().stream()
                .filter(e -> e.getCreatorUsername().equals(currentUser.getUsername()))
                .toList();

        if (owned.isEmpty()) {
            ConsoleUI.print("⚠️ You haven’t created any events.");
            return;
        }

        ConsoleUI.print("📋 Your Created Events:");
        for (int i = 0; i < owned.size(); i++) {
            ConsoleUI.print((i + 1) + " - " + owned.get(i).getDescription());
        }

        int index = ConsoleUI.promptInt("Select event to modify (0 to cancel):") - 1;
        if (index == -1) return;
        if (index < 0 || index >= owned.size()) {
            ConsoleUI.print("❌ Invalid selection.");
            return;
        }

        Event event = owned.get(index);

        ConsoleUI.print("""
        🛠️ Modify Options:
        1 - Update Event (location/time/tags/categories)
        2 - Delete Event
        3 - Cancel
    """);

        int action = ConsoleUI.promptInt("Choose an action:");

        switch (action) {
            case 1 -> handleUpdateEvent(event);
            case 2 -> handleDeleteEvent(event);
            default -> ConsoleUI.print("🔙 Cancelled.");
        }
    }

    private static void handleUpdateEvent(Event event) {
        String newLoc = ConsoleUI.prompt("New location:");
        LocalDateTime newTime = promptDateTime();
        List<Tag> newTags = promptTags();
        List<Category> newCats = promptCategories();

        UpdateEventCommand cmd = new UpdateEventCommand(event, newLoc, newTime, newTags, newCats, eventMap,currentUser);
        cmd.execute();
        commandHistory.push(cmd);
    }

    private static void handleDeleteEvent(Event event) {
        DeleteEventCommand cmd = new DeleteEventCommand(event, eventMap);
        cmd.execute();
        commandHistory.push(cmd);
    }




    private static List<Tag> promptTags() {
        List<Tag> result = new ArrayList<>();
        while (true) {
            ConsoleUI.print("Available Tags:");
            for (int i = 0; i < Tag.values().length; i++) {
                ConsoleUI.print((i + 1) + " - " + Tag.values()[i]);
            }

            String input = ConsoleUI.prompt("Enter up to 3 tag numbers:");
            String[] tokens = input.split(",");

            if (tokens.length > 3) {
                ConsoleUI.print("❌ Max 3 tags.");
                continue;
            }

            boolean allValid = true;
            for (String t : tokens) {
                try {
                    int idx = Integer.parseInt(t.trim()) - 1;
                    if (idx < 0 || idx >= Tag.values().length) {
                        allValid = false;
                        break;
                    }
                    Tag tag = Tag.values()[idx];
                    if (!result.contains(tag)) result.add(tag);
                } catch (Exception e) {
                    allValid = false;
                    break;
                }
            }

            if (allValid) break;
            ConsoleUI.print("❌ Invalid input. Try again.");
            result.clear();
        }
        return result;
    }

    private static List<Category> promptCategories() {
        List<Category> result = new ArrayList<>();
        while (true) {
            ConsoleUI.print("Available Categories:");
            for (int i = 0; i < Category.values().length; i++) {
                ConsoleUI.print((i + 1) + " - " + Category.values()[i]);
            }

            String input = ConsoleUI.prompt("Enter up to 3 category numbers:");
            String[] tokens = input.split(",");

            if (tokens.length > 3) {
                ConsoleUI.print("❌ Max 3 categories.");
                continue;
            }

            boolean allValid = true;
            for (String t : tokens) {
                try {
                    int idx = Integer.parseInt(t.trim()) - 1;
                    if (idx < 0 || idx >= Category.values().length) {
                        allValid = false;
                        break;
                    }
                    Category cat = Category.values()[idx];
                    if (!result.contains(cat)) result.add(cat);
                } catch (Exception e) {
                    allValid = false;
                    break;
                }
            }

            if (allValid) break;
            ConsoleUI.print("❌ Invalid input. Try again.");
            result.clear();
        }
        return result;
    }

    private static void selectTags(TagDecorator decorator) {
        while (true) {
            ConsoleUI.print("Available Tags:");
            for (int i = 0; i < Tag.values().length; i++) {
                ConsoleUI.print((i + 1) + " - " + Tag.values()[i]);
            }

            String input = ConsoleUI.prompt("Enter up to 3 tag numbers (comma-separated):");

            String[] tokens = input.split(",");
            if (tokens.length > 3) {
                ConsoleUI.print("❌ You can only select up to 3 tags. Try again.");
                continue;
            }

            List<Tag> selectedTags = new ArrayList<>();
            boolean allValid = true;

            for (String token : tokens) {
                try {
                    int index = Integer.parseInt(token.trim()) - 1;
                    if (index >= 0 && index < Tag.values().length) {
                        Tag tag = Tag.values()[index];
                        if (!selectedTags.contains(tag)) {
                            selectedTags.add(tag);
                        }
                    } else {
                        allValid = false;
                        ConsoleUI.print("❌ Invalid tag number: " + (index + 1));
                    }
                } catch (NumberFormatException e) {
                    allValid = false;
                    ConsoleUI.print("❌ Invalid number format: " + token.trim());
                }
            }

            if (allValid) {
                for (Tag t : selectedTags) {
                    decorator.addTag(t);
                }
                break; // success
            }

            ConsoleUI.print("🔁 Please enter valid tag numbers.");
        }
    }


    private static void selectCategories(CategoryDecorator decorator) {
        while (true) {
            ConsoleUI.print("Available Categories:");
            for (int i = 0; i < Category.values().length; i++) {
                ConsoleUI.print((i + 1) + " - " + Category.values()[i]);
            }

            String input = ConsoleUI.prompt("Enter up to 3 category numbers (comma-separated):");

            String[] tokens = input.split(",");
            if (tokens.length > 3) {
                ConsoleUI.print("❌ You can only select up to 3 categories. Try again.");
                continue;
            }

            List<Category> selectedCategories = new ArrayList<>();
            boolean allValid = true;

            for (String token : tokens) {
                try {
                    int index = Integer.parseInt(token.trim()) - 1;
                    if (index >= 0 && index < Category.values().length) {
                        Category cat = Category.values()[index];
                        if (!selectedCategories.contains(cat)) {
                            selectedCategories.add(cat);
                        }
                    } else {
                        allValid = false;
                        ConsoleUI.print("❌ Invalid category number: " + (index + 1));
                    }
                } catch (NumberFormatException e) {
                    allValid = false;
                    ConsoleUI.print("❌ Invalid number format: " + token.trim());
                }
            }

            if (allValid) {
                for (Category c : selectedCategories) {
                    decorator.addCategory(c);
                }
                break; // success
            }

            ConsoleUI.print("🔁 Please enter valid category numbers.");
        }
    }





    private static LocalDateTime promptDateTime() {
        while (true) {
            try {
                String date = ConsoleUI.prompt("Enter event date (YYYY-MM-DD):");
                String[] dateParts = date.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);

                String time = ConsoleUI.prompt("Enter event time (HH:MM):");
                String[] timeParts = time.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                return LocalDateTime.of(year, month, day, hour, minute);
            } catch (Exception e) {
                ConsoleUI.print("❌ Invalid input. Please try again.");
            }
        }
    }

    private static void authMenu() {
        while (currentUser == null) {
            ConsoleUI.print("\n🔐 Welcome to Event Manager!");
            ConsoleUI.print("1 - Login");
            ConsoleUI.print("2 - Register");
            ConsoleUI.print("3 - Exit");

            int choice = ConsoleUI.promptInt("Select an option:");

            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegistration();
                case 3 -> {
                    ConsoleUI.print("👋 Goodbye.");
                    System.exit(0);
                }
                default -> ConsoleUI.print("❌ Invalid choice.");
            }
        }
    }

    private static void handleLogin() {
        String username = ConsoleUI.prompt("Username:");
        String password = ConsoleUI.prompt("Password:");

        User user = userMap.get(username);

        if (user != null && user.verifyPassword(password)) {
            currentUser = user;
            ConsoleUI.print("✅ Login successful. Welcome, " + user.getUsername() + "!");
        } else {
            ConsoleUI.print("❌ Invalid credentials.");
        }
    }

    private static void handleRegistration() {
        String username = ConsoleUI.prompt("Choose a username:");
        if (userMap.containsKey(username)) {
            ConsoleUI.print("⚠️ Username already exists.");
            return;
        }

        String password = ConsoleUI.prompt("Choose a password:");
        User newUser = new User(username, password);
        userMap.put(username, newUser);
        ConsoleUI.print("🎉 Registered successfully. You can now log in.");
    }

    private static void browseAndRegisterForEvents() {
        if (eventMap.isEmpty()) {
            ConsoleUI.print("No events available.");
            return;
        }

        ConsoleUI.print("\n📋 Available Events:");
        List<String> keys = new ArrayList<>(eventMap.keySet());

        for (int i = 0; i < keys.size(); i++) {
            Event e = eventMap.get(keys.get(i));
            ConsoleUI.print((i + 1) + ". " + e.getDescription());
        }

        int choice = ConsoleUI.promptInt("Enter event number to register (0 to cancel):");

        if (choice == 0) return;

        if (choice < 1 || choice > keys.size()) {
            ConsoleUI.print("❌ Invalid event selection.");
            return;
        }

        Event selected = eventMap.get(keys.get(choice - 1));

        if (selected.getObservers().contains(currentUser)) {
            ConsoleUI.print("⚠️ You’re already registered for this event.");
            return;
        }

        selected.addObserver(currentUser);
        currentUser.addEvent(selected);

        ConsoleUI.print("✅ Registered for: " + selected.getName());
    }


}
