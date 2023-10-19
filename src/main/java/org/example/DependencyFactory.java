
package org.example;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.net.URI;

/**
 * The module containing all dependencies required by the {@link Handler}.
 */
public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */

    private static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
    private static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
    private static final String ENDPOINT_URI = System.getenv("AWS_ENDPOINT_URI");
    
    public static S3Client s3Client() {
        URI AWS_ENDPOINT_URI = URI.create(ENDPOINT_URI);
        if (AWS_ENDPOINT_URI == null) {
            try {
                AWS_ENDPOINT_URI = new URI("https://192.168.1.52");
            } catch(Exception e) {}
        }

        Region region = Region.US_EAST_1;
        AwsCredentials creds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        AwsCredentialsProvider awsCreds = StaticCredentialsProvider.create(creds);

        return S3Client.builder()
                        .region(region)
                        .credentialsProvider(awsCreds)
                        .endpointOverride(AWS_ENDPOINT_URI)
                        .build();
    }
}
