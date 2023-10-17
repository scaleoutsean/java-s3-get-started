
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
    public static S3Client s3Client() {
        URI myURI = null;
        try {
            myURI = new URI("https://s3.example.org");
        } catch(Exception e) {}

        Region region = Region.US_EAST_1;
        AwsCredentials creds = AwsBasicCredentials.create("AAAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        AwsCredentialsProvider awsCreds = StaticCredentialsProvider.create(creds);

        return S3Client.builder()
                        .region(region)
                        .credentialsProvider(awsCreds)
                        .endpointOverride(myURI)
                        .build();

        // return S3Client.builder()
                       // .httpClientBuilder(ApacheHttpClient.builder())
                       // .build();
    }
}
