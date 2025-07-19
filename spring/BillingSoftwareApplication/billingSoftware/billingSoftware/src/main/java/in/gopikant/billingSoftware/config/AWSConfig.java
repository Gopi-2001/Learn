package in.gopikant.billingSoftware.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// To mark this as configuration class
@Configuration
public class AWSConfig {

    @Value("${aws.access.key}")
    private String accesskey;
    @Value("${aws.secret.key}")
    private String secretkey;
    @Value("${aws.region}")
    private String region;

    @PostConstruct
    public void init() {
        System.out.println("AWS Region from properties: " + region);
    }
    // Creating a bean for S3 Client
    @Bean
    public S3Client s3Client(){
        // Making available S3 client to interact with S3 bucket
       return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey, secretkey)))
                .build();
    }
}
