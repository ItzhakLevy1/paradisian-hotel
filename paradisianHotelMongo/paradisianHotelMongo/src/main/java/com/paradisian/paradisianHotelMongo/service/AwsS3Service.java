package com.paradisian.paradisianHotelMongo.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;  // For AWS credentials
import com.amazonaws.auth.BasicAWSCredentials;           // Represents access and secret keys
import com.amazonaws.regions.Regions;                   // Enum for AWS regions
import com.amazonaws.services.s3.AmazonS3;              // AWS S3 client interface
import com.amazonaws.services.s3.AmazonS3ClientBuilder; // Builder for S3 client configuration
import com.amazonaws.services.s3.model.ObjectMetadata;  // Metadata for objects in S3
import com.amazonaws.services.s3.model.PutObjectRequest;// Request object for uploading files
import org.springframework.beans.factory.annotation.Value; // Used to inject values from properties files
import org.springframework.stereotype.Service;          // Marks this as a Spring service component
import org.springframework.web.multipart.MultipartFile; // Represents files uploaded in HTTP requests

import java.io.InputStream; // For handling file streams

@Service // Indicates that this class is a service component managed by Spring
public class AwsS3Service {

    private final String bucketName = "paradisian-hotel"; // S3 bucket name where images will be stored

    // Inject AWS access key from the application.properties or environment variables
    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    // Inject AWS secret key from the application.properties or environment variables
    @Value("${aws.s3.secret.key}")
    private String getAwsS3SecretKey;

    /**
     * This method saves an image file to AWS S3 and returns the public URL of the uploaded image.
     * @param photo The image file to be uploaded, represented as a MultipartFile.
     * @return The public URL of the uploaded image.
     */
    public String saveImageToS3(MultipartFile photo) {
        String s3LocationImage = null; // Holds the public URL of the uploaded image

        try {
            // Extract the file name from the uploaded file
            String s3FileName = photo.getOriginalFilename();

            // Create AWS credentials using the access and secret keys
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, getAwsS3SecretKey);

            // Build an S3 client with the provided credentials and region (e.g., US East 1)
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_1) // Set the AWS region (e.g., "us-east-1")
                    .build();

            // Get the input stream of the file to upload
            InputStream inputStream = photo.getInputStream();

            // Define metadata for the file (e.g., content type)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg"); // Set the content type to "image/jpeg"

            // Create a request to upload the file to the specified bucket
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);

            // Execute the request to upload the file to S3
            s3Client.putObject(putObjectRequest);

            // Construct and return the public URL of the uploaded image
            return "https://" + bucketName + ".s3.amazonaws.com/" + s3FileName;

        } catch (Exception e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
            // Throw a RuntimeException in case of any failure
            throw new RuntimeException(e);
        }
    }
}
