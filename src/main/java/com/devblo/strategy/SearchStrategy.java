package com.devblo.strategy;

import com.devblo.models.Event;

import java.util.List;

public interface SearchStrategy {
    List<Event> search(List<Event> events, String filter);
}
