package com.devblo.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.devblo.command.Command;
import com.devblo.observer.Observer;

public class User implements Observer {
    private final String username;
    private final String password;
    private List<Event> registeredEvents = new ArrayList<>();

    private final Stack<Command> commandStack = new Stack<>();



    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean verifyPassword(String input) {
        return this.password.equals(input);
    }

    public String getUsername() {
        return username;
    }

    public void addEvent(Event event) {
        registeredEvents.add(event);
    }

    public void removeEvent(Event event) {
        registeredEvents.remove(event);
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    @Override
    public void update(Event event) {
        System.out.println("User '" + username + "' was notified: Event '" + event.getName() + "' has been updated.");
    }

    public void pushCommand(Command cmd) {
        commandStack.push(cmd);
    }

    public Command popCommand() {
        return commandStack.isEmpty() ? null : commandStack.pop();
    }

    public boolean hasUndo() {
        return !commandStack.isEmpty();
    }
}
