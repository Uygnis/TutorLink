package com.csy.springbootauthbe.event.service;

import com.csy.springbootauthbe.common.sequence.SequenceGeneratorService;
import com.csy.springbootauthbe.event.dto.EventDTO;
import com.csy.springbootauthbe.event.entity.Event;
import com.csy.springbootauthbe.event.mapper.EventMapper;
import com.csy.springbootauthbe.event.repository.EventRepository;
import com.csy.springbootauthbe.event.utils.EventRequest;
import com.csy.springbootauthbe.event.utils.EventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGenerator;
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);


    @Override
    public Optional<EventDTO> getEventByEventId(String eventId) {
        logger.info("Get event: {}",eventId);
        Optional<Event> optionalEvent =  eventRepository.findByEventId(Long.valueOf(eventId));
        return optionalEvent.map(eventMapper::toDTO);
    }

    @Override
    public List<EventDTO> getEvents(String tutorId) {
        if(tutorId != null){
            return eventRepository.findEventsByTutorId(tutorId).stream().map(eventMapper::toDTO).toList();
        }
        return eventRepository.findAll().stream().map(eventMapper::toDTO).toList();
    }

    @Override
    public EventResponse createEvent(EventRequest request) {
        List<EventDTO> events = eventRepository.findEventsByTutorId(request.getTutorId()).stream().map(eventMapper::toDTO).toList();
        validateEvent(request, events);
        Long eventNumber = sequenceGenerator.getNextEventId();
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventId(eventNumber);
        eventDTO.setEnd(request.getEnd());
        eventDTO.setStart(request.getStart());
        eventDTO.setDescription(request.getDescription());
        eventDTO.setTutorId(request.getTutorId());
        Event saved = eventRepository.save(eventMapper.toEntity(eventDTO));
        return createEventResponse(saved);
    }

    private static void validateEvent(EventRequest request, List<EventDTO> events) {
        LocalDateTime start = LocalDateTime.parse(request.getStart());
        LocalDateTime end = LocalDateTime.parse(request.getEnd());
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before Start date");
        }
        for(EventDTO event : events){
            LocalDateTime oldStart = LocalDateTime.parse(event.getStart());
            LocalDateTime oldEnd = LocalDateTime.parse(event.getEnd());
            if (start.isBefore(oldEnd) && end.isAfter(oldStart)){
                throw new IllegalArgumentException("Event overlaps an existing event");
            }
        }

    }

    @Override
    public EventResponse updateEvent(String eventId, EventRequest eventRequest) {
        Event event = eventRepository.findByEventId(Long.valueOf(eventId)).orElseThrow(
                () -> new UsernameNotFoundException("Event not found")
        );
        EventDTO eventDTO = eventMapper.toDTO(event);
        eventDTO.setTutorId(eventRequest.getTutorId());
        eventDTO.setStart(eventRequest.getStart());
        eventDTO.setEnd(eventRequest.getEnd());
        eventDTO.setDescription(eventRequest.getDescription());
        Event saved = eventRepository.save(eventMapper.toEntity(eventDTO));
        return createEventResponse(saved);
    }

    @Override
    public void deleteEvent(String eventId) {
        Event event = eventRepository.findByEventId(Long.valueOf(eventId)).orElseThrow(
                () -> new UsernameNotFoundException("Event not found")
        );
        eventRepository.delete(event);
    }

    private EventResponse createEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .eventId(event.getEventId())
                .start(event.getStart())
                .end(event.getEnd())
                .description(event.getDescription())
                .tutorId(event.getTutorId())
                .build();
    }
}
