package com.csy.springbootauthbe.tutor.entity;

import com.csy.springbootauthbe.tutor.dto.TutorStagedProfileDTO;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tutors")
public class Tutor {

    @Id
    private String id;
    private String subject;
    private String userId;
    private Double hourlyRate;

    private List<QualificationFile> qualifications;
    private Map<String, Availability> availability;

    private String profileImageUrl;
    private List<String> lessonType;
    private String description;
    private String rejectedReason;

    private TutorStagedProfileDTO stagedProfile;
    private AccountStatus previousStatus;

    // 👇 prevent Lombok from generating the getter for reviews
    @Getter(AccessLevel.NONE)
    private List<Review> reviews;

    // custom safe getter and setter
    public List<Review> getReviews() {
        return reviews == null ? Collections.emptyList() : Collections.unmodifiableList(reviews);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews == null ? null : List.copyOf(reviews);
    }

}
