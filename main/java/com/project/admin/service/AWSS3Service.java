package com.project.admin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AWSS3Service {
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Service.class);
	 
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    @Value("${project.user.uploadDirectory}")
    private String filePath;
 
    public String uploadFile(String foldarName,String fileName, final MultipartFile multipartFile) {
        LOGGER.info("File upload in progress.");
        String uploadFileName = "";
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileName = uploadFileToS3Bucket(bucketName,foldarName,fileName,file);
            LOGGER.info("File upload is completed.");
            file.delete();  // To remove the file locally created in the project folder.
        } catch (final AmazonServiceException ex) {
            LOGGER.info("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
        }
        return uploadFileName;
    }
 
    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
        }
        return file;
    }
 
    private String uploadFileToS3Bucket(final String bucketName, String foldarName,String fileName,final File file) {
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,foldarName + fileName, file);
        amazonS3.putObject(putObjectRequest);
        return fileName;
    }
    
    public String deleteFile(String fileName) {
    	amazonS3.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }
    
    public ResponseEntity<?> downloadFile(String uuid,String fileName) {
    	String s3BucketfilePath = uuid + "/" + fileName;
		S3Object s3Object = amazonS3.getObject(bucketName, s3BucketfilePath);
//    	S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
        	/*String extention = fileName.substring(fileName.indexOf(".") + 1);
        	if (extention.equalsIgnoreCase("heic")) {
        		return new ResponseEntity<>(inputStream, HttpStatus.OK);
			} else {*/
				return new ResponseEntity<>(IOUtils.toByteArray(inputStream), HttpStatus.OK);
//			}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public File readFile(String uuid,String fileName) {
    	try {
			File directory = new File(filePath);
			if (!directory.exists()) {
				directory.mkdir();
				directory.setExecutable(true, false);
				directory.setReadable(true, false);
				directory.setWritable(true, false);
			}
			String path = filePath + uuid + "/";
			directory = new File(path);
			if (!directory.exists()) {
				directory.mkdir();
				directory.setExecutable(true, false);
				directory.setReadable(true, false);
				directory.setWritable(true, false);
			}
			path = path + fileName;
			File file = new File(path);
			file.createNewFile();
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			
			String s3BucketfilePath = uuid + "/" + fileName;
			S3Object s3Object = amazonS3.getObject(bucketName, s3BucketfilePath);
			S3ObjectInputStream inputStream = s3Object.getObjectContent();
			OutputStream outStream = new FileOutputStream(file);
			IOUtils.copy(inputStream, outStream);
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
}
