package com.devblo.observer;

import com.devblo.models.Event;

public interface Observer {
    void update(Event event);
}
