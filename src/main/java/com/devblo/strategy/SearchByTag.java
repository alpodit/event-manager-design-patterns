package com.devblo.strategy;

import com.devblo.models.Event;
import com.devblo.decorators.TagDecorator;
import com.devblo.decorators.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByTag implements SearchStrategy {
    @Override
    public List<Event> search(List<Event> events, String filter) {
        return events.stream()
                .filter(e -> {
                    if (e instanceof TagDecorator tagEvent) {
                        return tagEvent.getTags().stream()
                                .anyMatch(tag -> tag.name().toLowerCase().contains(filter.toLowerCase()));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
