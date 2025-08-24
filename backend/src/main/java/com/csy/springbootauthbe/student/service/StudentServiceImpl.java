package com.csy.springbootauthbe.student.service;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.entity.Student;
import com.csy.springbootauthbe.student.mapper.StudentMapper;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.student.utils.TutorSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        log.info("Creating student with data: {}", studentDTO);
        Student student = studentMapper.toEntity(studentDTO);
        Student saved = studentRepository.save(student);
        log.info("Student saved with ID: {}", saved.getId());
        return studentMapper.toDTO(saved);
    }

    @Override
    public Optional<StudentDTO> getStudentByUserId(String userId) {
        log.debug("Fetching student by userId: {}", userId);
        return studentRepository.findByUserId(userId).map(studentMapper::toDTO);
    }

    @Override
    public List<Document> searchTutors(TutorSearchRequest req) {
        log.info("Searching tutors with request: {}", req);

        List<AggregationOperation> ops = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();

        // 1. Convert userId string to ObjectId for lookup
        ops.add(Aggregation.addFields()
                .addFieldWithValue("userIdObj", new Document("$toObjectId", "$userId"))
                .build());

        // 2. Lookup user document
        ops.add(Aggregation.lookup("users", "userIdObj", "_id", "user"));
        ops.add(Aggregation.unwind("user"));
        log.debug("Performed addFields, lookup, and unwind for user");

        // 3. Flatten user fields into root document
        ops.add(Aggregation.addFields()
                .addFieldWithValue("firstname", "$user.firstname")
                .addFieldWithValue("lastname", "$user.lastname")
                .addFieldWithValue("email", "$user.email")
                .build());
        log.debug("Flattened user fields into root document");

        // 4. Build dynamic filters
        if (req.getName() != null) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("firstname").regex(req.getName(), "i"),
                    Criteria.where("lastname").regex(req.getName(), "i")
            ));
            log.debug("Filter added: name like {}", req.getName());
        }

        if (req.getSubject() != null) {
            criteriaList.add(Criteria.where("subject").regex(req.getSubject(), "i"));
            log.debug("Filter added: subject like {}", req.getSubject());
        }

        if (req.getMinPrice() != null && req.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("hourlyRate")
                    .gte(req.getMinPrice())
                    .lte(req.getMaxPrice()));
            log.debug("Filter added: hourlyRate between {} and {}", req.getMinPrice(), req.getMaxPrice());
        }

        if (req.getAvailability() != null) {
            String dbKey = normalizeDay(req.getAvailability());
            if (dbKey != null) {
                criteriaList.add(Criteria.where("availability." + dbKey + ".enabled").is(true));
                log.debug("Filter added: availability on {}", dbKey);
            } else {
                log.warn("Invalid availability day provided: {}", req.getAvailability());
            }
        }

        // 5. Add match stage if any filters exist
        if (!criteriaList.isEmpty()) {
            Criteria combined = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            ops.add(Aggregation.match(combined));
            log.debug("Added match stage with criteria: {}", combined.getCriteriaObject());
        }

        // 6. Project only needed fields
        ops.add(Aggregation.project("subject", "hourlyRate", "availability", "firstname", "lastname", "email"));
        log.debug("Added project stage");

        // 7. Execute aggregation
        Aggregation aggregation = Aggregation.newAggregation(ops);
        log.info("Executing aggregation pipeline: {}", aggregation);

        List<Document> results = mongoTemplate.aggregate(aggregation, "tutors", Document.class).getMappedResults();
        log.info("Found {} tutors", results.size());

        if (log.isDebugEnabled()) {
            results.forEach(doc -> log.debug("Tutor result: {}", doc.toJson()));
        }

        return results;
    }


    /* Helper Class */
    private static final Map<String, String> DAY_MAP = Map.ofEntries(
            Map.entry("MONDAY", "Mon"),
            Map.entry("TUESDAY", "Tue"),
            Map.entry("WEDNESDAY", "Wed"),
            Map.entry("THURSDAY", "Thu"),
            Map.entry("FRIDAY", "Fri"),
            Map.entry("SATURDAY", "Sat"),
            Map.entry("SUNDAY", "Sun")
    );

    private String normalizeDay(String input) {
        if (input == null) return null;
        return DAY_MAP.get(input.trim().toUpperCase()); // returns e.g. "Mon"
    }
}
