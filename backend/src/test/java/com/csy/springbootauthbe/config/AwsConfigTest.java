package com.csy.springbootauthbe.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class AwsConfigTest {

    private AwsConfig awsConfig;

    @BeforeEach
    void setUp() throws Exception {
        awsConfig = new AwsConfig();

        // Inject @Value fields manually for testing
        setField("accessKey", "dummy-access");
        setField("secretKey", "dummy-secret");
        setField("region", "us-east-1");
    }

    private void setField(String name, Object value) throws Exception {
        Field f = AwsConfig.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(awsConfig, value);
    }

    @Test
    void s3Client_buildsClientWithCorrectConfig() {
        S3Client client = awsConfig.s3Client();

        assertNotNull(client);
        assertEquals(Region.of("us-east-1"), client.serviceClientConfiguration().region());

        // Verify credentials provider type and contents
        var provider = client.serviceClientConfiguration().credentialsProvider();
        assertTrue(provider instanceof StaticCredentialsProvider);

        var creds = ((StaticCredentialsProvider) provider).resolveCredentials();
        assertEquals("dummy-access", creds.accessKeyId());
        assertEquals("dummy-secret", creds.secretAccessKey());
    }

    @Test
    void s3Client_returnsNewInstanceEachTime() {
        S3Client c1 = awsConfig.s3Client();
        S3Client c2 = awsConfig.s3Client();
        assertNotSame(c1, c2, "Each call should produce a new S3Client instance");
    }
}
