package com.devblo.strategy;

import com.devblo.models.Event;

import java.util.List;

public class ShowAllStrategy implements SearchStrategy {
    @Override
    public List<Event> search(List<Event> events, String filter) {
        return events; // ignores filter
    }
}
