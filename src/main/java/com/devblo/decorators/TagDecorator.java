package com.devblo.decorators;

import com.devblo.models.Event;

import java.util.ArrayList;
import java.util.List;

public class TagDecorator extends EventDecorator {
    private final List<Tag> tags = new ArrayList<>();

    public TagDecorator(Event event) {
        super(event);
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag) && tags.size() < 3) {
            tags.add(tag);
        }
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public String getDescription() {
        return event.getDescription() + " | Tags: " + getTags();
    }
}
