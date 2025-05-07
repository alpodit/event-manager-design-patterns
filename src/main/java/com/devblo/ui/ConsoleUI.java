package com.devblo.ui;

import org.jline.reader.*;
import org.jline.reader.impl.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class ConsoleUI {
    private static LineReader reader;

    static {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            Parser parser = new DefaultParser();
            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .build();

        } catch (IOException e) {
            System.err.println("Failed to initialize console: " + e.getMessage());
            System.exit(1);
        }
    }

    public static String prompt(String message) {
        return reader.readLine(message + " ");
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static int promptInt(String message) {
        while (true) {
            try {
                String input = reader.readLine(message + " ");
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                print("Please enter a valid number.");
            }
        }
    }
}
