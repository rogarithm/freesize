package org.rogarithm.presize.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${cloud.aws.region}")
    private String region;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKeyId;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        StaticCredentialsProvider credentialInfo = StaticCredentialsProvider.create(
                AwsBasicCredentials.builder()
                        .accessKeyId(accessKeyId)
                        .secretAccessKey(secretKey)
                        .build()
        );

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialInfo)
                .build();
    }
}
