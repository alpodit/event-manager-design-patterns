package com.devblo.strategy;

import com.devblo.models.Event;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByDate implements SearchStrategy {
    @Override
    public List<Event> search(List<Event> events, String filter) {
        return events.stream()
                .filter(e -> e.getDateTime().toLocalDate().toString().contains(filter))
                .collect(Collectors.toList());
    }
}
