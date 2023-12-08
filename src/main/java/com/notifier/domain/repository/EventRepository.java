package com.notifier.domain.repository;

import com.notifier.domain.model.Event;

import java.util.List;

public interface EventRepository {

    List<Event> findAll();

    // Additional methods for CRUD operations or custom queries
}
