package org.example;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Bucket;
import java.io.File;
import java.lang.String;
import java.util.*;

public class Handler {
    private final S3Client s3Client;

    public Handler() {
        s3Client = DependencyFactory.s3Client();
    }

    public void sendRequest() {
        // String bucket = "native" + System.currentTimeMillis();
        String bucket = "native";
        String key = "ontap-s3-test.txt";
        String objectPath = "/tmp/ontap-s3-test.txt";

        // createBucket(s3Client, bucket);

        System.out.println("===> Uploading object...");

        putMyS3Object(s3Client, bucket, key, objectPath);

        // System.out.println("===> Upload complete, please use another shell to list bucket contents!");
        // System.out.printf("%n");
        
	    System.out.println("===> List ONTAP S3 buckets...");
        ListMyBuckets(s3Client);
        
        System.out.println("===> List bucket objects...");
        listMyObjects(s3Client, bucket);
        
        System.out.println("\n\n===> Object will be deleted from bucket in 30 seconds!");
        
        // pause for 30 seconds
        try {
	        Thread.sleep(30000);
        } catch(Exception e) {}
        
        cleanUp(s3Client, bucket, key);

        System.out.println("Closing the connection to {S3}");
        s3Client.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName + " is ready.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
        System.out.println("===> Cleaning up...");
        try {
            System.out.println("===> Deleting object: " + keyName);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("===> Object " + keyName + " has been deleted.");
            // System.out.println("Deleting bucket: " + bucketName);
            // DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            // s3Client.deleteBucket(deleteBucketRequest);
            System.out.println("===> Bucket " + bucketName + " has NOT been deleted; you have to use the ONTAP API for that.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("===> Cleanup complete!");
        System.out.printf("%n");
    }

    public static void listMyObjects(S3Client s3, String bucketName ) {
        System.out.println("===> Bucket " + bucketName + " has the following object(s):");
        System.out.printf("%n");
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

            ListObjectsResponse res = s3.listObjects(listObjects);

            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.print("\n===> The name of the key (i.e. object) is " + myValue.key());
                // System.out.print("\n===> The object size is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n===> The object size is " + myValue.size() + " bytes");
                // See if you can use the ONTAP API for this
                // System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val/1024;
    }

    public static void putMyS3Object(S3Client s3, String bucketName, String objectKey, String objectPath) {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(metadata)
                .build();

            s3.putObject(putOb, RequestBody.fromFile(new File(objectPath)));
            System.out.println("===> Successfully PUT object named " + objectKey + " into bucket named " + bucketName);

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void ListMyBuckets(S3Client s3) {
        List<Bucket> buckets = s3.listBuckets().buckets();
        System.out.println("==> Available buckets:");

        for (Bucket b : buckets) {
            System.out.println(b.name());
        }
    }
}
