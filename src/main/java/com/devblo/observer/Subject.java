package com.devblo.observer;

import com.devblo.observer.Observer;

import java.util.List;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);

    List<Observer> getObservers();

    void notifyObservers();
}
