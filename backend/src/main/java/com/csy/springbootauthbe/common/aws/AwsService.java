package com.csy.springbootauthbe.common.aws;

import com.csy.springbootauthbe.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    public String bucketName;

    public AwsResponse uploadFile(MultipartFile file, String folder) {
        String key = folder + "/" + file.getOriginalFilename();
        AwsResponse res = new AwsResponse();
        try {
            PutObjectResponse response =s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .checksumAlgorithm(ChecksumAlgorithm.SHA256)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            res.setHash(response.checksumSHA256());
            res.setKey(key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return res; // or s3Client.utilities().getUrl(...) to get full URL
    }

    public AwsResponse uploadProfilePic(MultipartFile file, String folder) {
        // Generate unique key
        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        AwsResponse res = new AwsResponse();

        try {
            PutObjectResponse response = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .checksumAlgorithm(ChecksumAlgorithm.SHA256)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Populate response
            res.setKey(key);
            res.setHash(response.checksumSHA256());

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile pic", e);
        }

        return res;
    }


    public void deleteProfilePic(String key) {
        try {
            s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete profile pic from S3: " + key, e);
        }
    }

    public String extractKeyFromUrl(String url) {
        if (url == null) return null;
        // Example URL: https://bucket-name.s3.us-east-1.amazonaws.com/profilePicture/abc.jpg
        int idx = url.indexOf(".amazonaws.com/");
        if (idx == -1) return null;
        return url.substring(idx + ".amazonaws.com/".length());
    }






}
