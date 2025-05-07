package com.devblo.decorators;

import com.devblo.models.Event;

import java.util.ArrayList;
import java.util.List;

public class CategoryDecorator extends EventDecorator {
    private final List<Category> categories = new ArrayList<>();

    public CategoryDecorator(Event event) {
        super(event);
    }

    public void addCategory(Category category) {
        if (!categories.contains(category) && categories.size() < 3) {
            categories.add(category);
        }
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public String getDescription() {
        return event.getDescription() + " | Categories: " + getCategories();
    }
}
