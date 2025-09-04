package com.csy.springbootauthbe.common.aws;

import com.csy.springbootauthbe.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
    private String bucketName;

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


}
