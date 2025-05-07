package com.devblo.strategy;

import com.devblo.models.Event;
import com.devblo.decorators.CategoryDecorator;
import com.devblo.decorators.Category;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByCategory implements SearchStrategy {
    @Override
    public List<Event> search(List<Event> events, String filter) {
        return events.stream()
                .filter(e -> {
                    if (e instanceof CategoryDecorator catEvent) {
                        return catEvent.getCategories().stream()
                                .anyMatch(cat -> cat.name().toLowerCase().contains(filter.toLowerCase()));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
