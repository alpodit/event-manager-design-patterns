package com.devblo.command;

import java.util.Stack;

public class CommandHistory {
    private Stack<Command> history = new Stack<>();

    public void push(Command cmd) {
        history.push(cmd);
    }

    public void undoLast() {
        if (history.isEmpty()) {
            System.out.println("⚠️ Nothing to undo.");
            return;
        }

        Command last = history.pop();
        last.undo();
    }
}
