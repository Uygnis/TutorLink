package com.csy.springbootauthbe.student.service;

import com.csy.springbootauthbe.common.sequence.SequenceGeneratorService;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.dto.TutorProfileDTO;
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
import org.bson.types.ObjectId;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        log.info("Creating student with data: {}", studentDTO);

        // generate next student number here
        String studentNumber = sequenceGenerator.getNextStudentId();
        studentDTO.setStudentNumber(studentNumber);

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
    public List<TutorProfileDTO> searchTutors(TutorSearchRequest req) {
        log.info("Searching tutors with request: {}", req);

        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.addFields()
                .addFieldWithValue("userIdObj", new Document("$toObjectId", "$userId"))
                .build());

        ops.add(Aggregation.lookup("users", "userIdObj", "_id", "user"));
        ops.add(Aggregation.unwind("user", true));

        ops.add(Aggregation.addFields()
                .addFieldWithValue("firstname", "$user.firstname")
                .addFieldWithValue("lastname", "$user.lastname")
                .addFieldWithValue("email", "$user.email")
                .build());

        // Apply filters (same as before) ...
        List<Criteria> criteriaList = new ArrayList<>();
        if (req.getName() != null) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("firstname").regex(req.getName(), "i"),
                    Criteria.where("lastname").regex(req.getName(), "i")
            ));
        }
        if (req.getSubject() != null) {
            criteriaList.add(Criteria.where("subject").regex(req.getSubject(), "i"));
        }
        if (req.getMinPrice() != null && req.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("hourlyRate")
                    .gte(req.getMinPrice())
                    .lte(req.getMaxPrice()));
        }
        if (req.getAvailability() != null) {
            String dbKey = normalizeDay(req.getAvailability());
            if (dbKey != null) {
                criteriaList.add(Criteria.where("availability." + dbKey + ".enabled").is(true));
            }
        }

        if (!criteriaList.isEmpty()) {
            ops.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }

        ops.add(Aggregation.project("subject", "hourlyRate", "availability", "firstname", "lastname", "email", "profileImage", "description"));

        Aggregation aggregation = Aggregation.newAggregation(ops);
        List<Document> docs = mongoTemplate.aggregate(aggregation, "tutors", Document.class).getMappedResults();

        // Map to TutorDTO
        List<TutorProfileDTO> tutors = new ArrayList<>();
        for (Document doc : docs) {
            tutors.add(mapToTutorDTO(doc));
        }
        return tutors;
    }

    @Override
    public Optional<TutorProfileDTO> getTutorById(String tutorId) {
        log.info("Fetching tutor details by ID: {}", tutorId);

        List<AggregationOperation> ops = new ArrayList<>();
        ops.add(Aggregation.addFields()
                .addFieldWithValue("userIdObj", new Document("$toObjectId", "$userId"))
                .build());

        ops.add(Aggregation.lookup("users", "userIdObj", "_id", "user"));
        ops.add(Aggregation.unwind("user", true));

        ops.add(Aggregation.addFields()
                .addFieldWithValue("firstname", "$user.firstname")
                .addFieldWithValue("lastname", "$user.lastname")
                .addFieldWithValue("email", "$user.email")
                .build());

        ops.add(Aggregation.match(Criteria.where("_id").is(new ObjectId(tutorId))));

        ops.add(Aggregation.project("subject", "hourlyRate", "availability", "firstname", "lastname", "email", "profileImage", "description"));

        Aggregation aggregation = Aggregation.newAggregation(ops);
        List<Document> docs = mongoTemplate.aggregate(aggregation, "tutors", Document.class).getMappedResults();

        if (docs.isEmpty()) return Optional.empty();
        return Optional.of(mapToTutorDTO(docs.get(0)));
    }

    /* Helper Mapper */
    private TutorProfileDTO mapToTutorDTO(Document doc) {
        TutorProfileDTO dto = new TutorProfileDTO();
        dto.setId(doc.getObjectId("_id").toHexString());
        dto.setFirstname(doc.getString("firstname"));
        dto.setLastname(doc.getString("lastname"));
        dto.setSubject(doc.getString("subject"));
        dto.setHourlyRate(doc.getDouble("hourlyRate"));
        dto.setAvailability((Map<String, Object>) doc.get("availability"));
        return dto;
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
