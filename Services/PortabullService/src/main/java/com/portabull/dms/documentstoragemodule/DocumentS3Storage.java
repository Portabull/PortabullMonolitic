package com.portabull.dms.documentstoragemodule;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.portabull.constants.FileConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.response.DocumentResponse;
import com.portabull.utils.DocumentUtils;
import com.portabull.dms.utils.StorageResponseGenerator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentS3Storage implements DocumentStorageModule {


    /**
     * Amazon S3 Env starts
     */

    @Value("${application.bucket.name}")
    String bucketName;

    @Value("${cloud.aws.credentials.access-key}")
    String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    String accessSecret;

    @Value("${cloud.aws.region.static}")
    String region;

    @Value("${dms.storage.classtype}")
    private String classType;

    /**
     * Amazon S3 Env ends
     */


    AmazonS3 s3Client;

    @Autowired
    StorageResponseGenerator storageResponseGenerator;

    @Autowired
    HibernateUtils hibernateUtils;

    static Logger logger = LoggerFactory.getLogger(DocumentS3Storage.class);

    @Override
    public DocumentResponse uploadDocument(InputStream inputStream, String fileName) throws IOException {

        File file = DocumentUtils.createFile(inputStream, fileName);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));

        DocumentUtils.deleteFile(file);

        return new DocumentResponse(MessageConstants.DOCUMENT_UPLOAD_SUCCESS, 200L,
                PortableConstants.SUCCESS, null);
    }


    @Override
    public DocumentResponse uploadDocument(InputStream inputStream, String folderName, String fileName) throws IOException {

        File file = DocumentUtils.createFile(inputStream, fileName);

        String path = folderName + FileConstants.FILE_SEPARATOR + fileName;
        s3Client.putObject(new PutObjectRequest(bucketName, path, file));

        DocumentUtils.deleteFile(file);

        return new DocumentResponse(MessageConstants.DOCUMENT_UPLOAD_SUCCESS, 200L,
                PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse downloadDocument(String eLocation) {
        try (S3Object s3Object = s3Client.getObject(bucketName, eLocation)) {
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return new DocumentResponse().
                        setMessage(MessageConstants.DOWNLOAD_SUCCESS).setStatus(PortableConstants.SUCCESS).
                        setFileResponse(StorageResponseGenerator.prepareFileResponse(new ByteArrayInputStream(IOUtils.toByteArray(inputStream))));
            }
        } catch (Exception e) {
            logger.error("While downloading document it throws error", e);
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.DOWNLOADING_FAILED, 500L, PortableConstants.FAILED);
        }

    }

    @Override
    public DocumentResponse deleteDocument(String eLocation) {

        s3Client.deleteObject(bucketName, eLocation);

        return storageResponseGenerator.generateDocumentResponse(eLocation).
                setMessage(MessageConstants.DOCUMENT_DELETE_SUCCESS).setStatus(PortableConstants.SUCCESS);
    }

    @Override
    public DocumentResponse downloadDocumentBytes(String eLocation) {
        try (S3Object s3Object = s3Client.getObject(bucketName, eLocation)) {
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return storageResponseGenerator.generateDocumentResponse(eLocation).
                        setMessage("Successfully downloaded Bytes").setStatus(PortableConstants.SUCCESS).
                        setFileResponse(StorageResponseGenerator.prepareFileResponse(inputStream));
            }
        } catch (Exception e) {
            logger.error("While downloading document bytes it throws error", e);
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.DOWNLOADING_FAILED, 500L, PortableConstants.FAILED);
        }
    }

    @Override
    public DocumentResponse transferDocument() {
        return new DocumentResponse();
    }

    public Bucket createBucket(String bucketName) {
        return s3Client.createBucket(bucketName);
    }

    /**
     * Amazon S3 Configuration
     *
     * @return
     */

    @PostConstruct
    public void s3Client() throws ClassNotFoundException {
        if (DocumentS3Storage.class.getSimpleName().equalsIgnoreCase(Class.forName(classType).getSimpleName())) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, accessSecret)))
                    .withRegion(region).build();

            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
                logger.info("Created Bucket Successfully BucketName : {}", bucketName);
            }
        }
    }
}
