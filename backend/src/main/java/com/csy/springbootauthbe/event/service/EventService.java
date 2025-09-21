package com.csy.springbootauthbe.event.service;

import com.csy.springbootauthbe.event.dto.EventDTO;
import com.csy.springbootauthbe.event.utils.EventRequest;
import com.csy.springbootauthbe.event.utils.EventResponse;
import com.csy.springbootauthbe.student.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

import com.csy.springbootauthbe.student.dto.TutorProfileDTO;
import com.csy.springbootauthbe.student.utils.TutorSearchRequest;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

    Optional<EventDTO> getEventByEventId(String eventId);

    List<EventDTO> getEvents();

    EventResponse createEvent(EventRequest request);

    EventResponse updateEvent(String eventId, EventRequest eventRequest);

    void deleteEvent(String eventId);



}
