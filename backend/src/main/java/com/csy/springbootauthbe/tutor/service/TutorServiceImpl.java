package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.common.aws.AwsResponse;
import com.csy.springbootauthbe.common.aws.AwsService;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.entity.Student;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TutorServiceImpl implements TutorService {

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final AwsService awsService;

    private static final String DEFAULT_PROFILE_URL =
            "https://tutorlink-s3.s3.us-east-1.amazonaws.com/profilePicture/default-profile-pic.jpg";

    @Override
    public TutorDTO createTutor(TutorDTO tutorDTO) {
        Tutor tutor = tutorMapper.toEntity(tutorDTO);
        Tutor saved = tutorRepository.save(tutor);
        return tutorMapper.toDTO(saved);
    }

    @Override
    public Optional<TutorDTO> getTutorByUserId(String userId) {
        return tutorRepository.findByUserId(userId).map(tutorMapper::toDTO);
    }

    @Override
    public TutorResponse updateTutor(String userId, TutorRequest updatedData)
            throws NoSuchAlgorithmException, IOException {

        Tutor tutor = tutorRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor not found"));

        tutor.setHourlyRate(updatedData.getHourlyRate());
        tutor.setAvailability(updatedData.getAvailability());

        List<QualificationFile> qualifications = tutor.getQualifications();
        Set<String> newHashes = new HashSet<>();

        // Handle new file uploads
        if (updatedData.getFileUploads() != null) {
            for (MultipartFile file : updatedData.getFileUploads()) {
                String hash = hash(file);

                // Add hash to newHashes set
                newHashes.add(hash);

                // Only upload if not already in DB
                boolean exists = qualifications.stream()
                        .anyMatch(f -> f.getHash().equals(hash));
                if (!exists) {
                    QualificationFile qFile = new QualificationFile();
                    qFile.setName(file.getOriginalFilename());
                    qFile.setType(file.getContentType());
                    qFile.setUploadedAt(new Date());
                    qFile.setHash(hash);

                    AwsResponse awsRes = awsService.uploadFile(file, userId);
                    qFile.setPath(awsRes.getKey());
                    qFile.setDeleted(false);

                    qualifications.add(qFile);
                }
            }
        }

        //include existing metadata from updatedData
        if (updatedData.getQualifications() != null) {
            for (QualificationFile metaFile : updatedData.getQualifications()) {
                newHashes.add(metaFile.getHash());
            }
        }

        // Update deleted status
        for (QualificationFile oldFile : qualifications) {
            oldFile.setDeleted(!newHashes.contains(oldFile.getHash()));
            if (!oldFile.isDeleted() && oldFile.getUpdatedAt() != null) {
                oldFile.setUpdatedAt(null); // reset deletedAt if file is now active
            } else if (oldFile.isDeleted() && oldFile.getUpdatedAt() == null) {
                oldFile.setUpdatedAt(new Date()); // mark deletion timestamp
            }
        }

        tutor.setQualifications(qualifications);

        tutorRepository.save(tutor);

        return createTutorResponse(tutor);
    }

    @Override
    public TutorDTO updateProfilePicture(String tutorId, MultipartFile file) {
        log.info("Updating profile picture for studentId: {}", tutorId);

        Tutor tutor = tutorRepository.findByUserId(tutorId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Delete old profile picture if it's not default
        if (tutor.getProfileImageUrl() != null &&
                !tutor.getProfileImageUrl().equals(DEFAULT_PROFILE_URL)) {
            String oldKey = awsService.extractKeyFromUrl(tutor.getProfileImageUrl());
            if (oldKey != null) {
                awsService.deleteProfilePic(oldKey);
                log.info("Deleted old profile picture from S3: {}", oldKey);
            }
        }

        // Upload new file and get hash + key
        AwsResponse uploadRes = awsService.uploadProfilePic(file, "profilePicture");
        String newKey = uploadRes.getKey();
        String newHash = uploadRes.getHash();

        // Construct public URL
        String fileUrl = "https://" + awsService.bucketName + ".s3.amazonaws.com/" + newKey;
        log.info("Uploaded new profile picture: {}, hash: {}", fileUrl, newHash);

        tutor.setProfileImageUrl(fileUrl);

        Tutor saved = tutorRepository.save(tutor);
        return tutorMapper.toDTO(saved);
    }




    private String hash(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(file.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public void deleteTutor(String userId) {
            Tutor tutor = tutorRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("Tutor not found"));
        tutorRepository.delete(tutor);
    }

    private TutorResponse createTutorResponse(Tutor tutor) {
        return TutorResponse.builder()
                .id(tutor.getId())
                .hourlyRate(tutor.getHourlyRate())
                .qualifications(tutor.getQualifications())
                .availability(tutor.getAvailability())
                .build();
    }


}