package com.csy.springbootauthbe.event.repository;

import com.csy.springbootauthbe.event.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    Optional<Event> findByEventId(Long eventId);

    List<Event> findEventsByTutorId(String tutorId);
}

