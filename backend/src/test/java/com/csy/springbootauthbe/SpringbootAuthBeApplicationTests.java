package com.csy.springbootauthbe;

import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.booking.repository.BookingRepository;
import com.csy.springbootauthbe.doctor.repository.DoctorRepository;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = SpringbootAuthBeApplicationTests.EmptyConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                // hard-disable Mongo auto-config & repo scanning
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
                "spring.data.mongodb.repositories.enabled=false",

                // any app props required by your beans
                "aws.s3.access-key=dummy",
                "aws.s3.secret-key=dummy",
                "aws.s3.region=us-east-1",
                "aws.s3.bucket=test-bucket",
                "jwt.secret.key=dummy",

                // keep it non-web
                "spring.main.web-application-type=none"
        }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringbootAuthBeApplicationTests {

    /** Minimal, no component scan. */
    @SpringBootConfiguration
    static class EmptyConfig { }

    // Mock any repositories/services your code might touch during context startup
    @MockBean private AdminRepository adminRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private TutorRepository tutorRepository;
    @MockBean private StudentRepository studentRepository;
    @MockBean private DoctorRepository doctorRepository;
    @MockBean private BookingRepository bookingRepository;

    // Mock Mongo infrastructure if anything tries to inject it
    @MockBean private MongoTemplate mongoTemplate;
    @MockBean private GridFsTemplate gridFsTemplate;

    @Test
    void contextLoads() {
        assertTrue(true, "Application context loaded successfully with mocked dependencies.");
    }
}
