package com.csy.springbootauthbe.event.controller;

import com.csy.springbootauthbe.event.dto.EventDTO;
import com.csy.springbootauthbe.event.service.EventService;
import com.csy.springbootauthbe.event.utils.EventRequest;
import com.csy.springbootauthbe.event.utils.EventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping("/")
    public ResponseEntity<List<EventDTO>> getEvents() {
        List<EventDTO> events = eventService.getEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<EventDTO>> getEventsByTutor(@PathVariable String tutorId) {
        List<EventDTO> events = eventService.getEventsByTutor(tutorId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable String eventId) {
        Optional<EventDTO> eventOpt = eventService.getEventByEventId(eventId);
        return eventOpt.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable String eventId, @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Event info deleted successfully.");
    }


}
