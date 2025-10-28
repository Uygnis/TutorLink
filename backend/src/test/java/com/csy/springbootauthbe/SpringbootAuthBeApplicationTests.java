package com.csy.springbootauthbe;

import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.booking.repository.BookingRepository;
import com.csy.springbootauthbe.doctor.repository.DoctorRepository;
import com.csy.springbootauthbe.notification.repository.NotificationRepository;
import com.csy.springbootauthbe.notification.service.NotificationService;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = SpringbootAuthBeApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class // disables Mongo repo scanning entirely
})
@TestPropertySource(properties = {
        // disable Mongo + auditing
        "app.mongo.enabled=false",
        "spring.data.mongodb.repositories.enabled=false",
        "spring.data.mongodb.auditing.enabled=false",
        "spring.main.web-application-type=none",
        "spring.main.allow-bean-definition-overriding=true",

        // dummy environment vars
        "aws.s3.access-key=dummy",
        "aws.s3.secret-key=dummy",
        "aws.s3.region=us-east-1",
        "aws.s3.bucket=test-bucket",
        "jwt.secret.key=dummy",
        "stripe.secret-key=dummy"
})
class SpringbootAuthBeApplicationTests {

    // --- Core Repositories (mocked to avoid Mongo startup) ---
    @MockBean private AdminRepository adminRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private TutorRepository tutorRepository;
    @MockBean private StudentRepository studentRepository;
    @MockBean private DoctorRepository doctorRepository;
    @MockBean private BookingRepository bookingRepository;

    // --- Services that depend on repositories ---
    @MockBean private WalletService walletService;
    @MockBean private NotificationService notificationService;

    // --- Supporting infra that would otherwise trigger Mongo beans ---
    @MockBean private MongoTemplate mongoTemplate;
    @MockBean private GridFsTemplate gridFsTemplate;
    @MockBean private NotificationRepository notificationRepository;

    @Test
    void contextLoads() {
        assertTrue(true); // verifies Spring context starts cleanly
    }
}
