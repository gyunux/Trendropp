package com.celebstyle.api.common;

import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3UploadService {
    private final S3Client s3Client;
    private final String bucketName = "celebstyletest-storage";

    @Autowired
    public S3UploadService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file,String directory) throws IOException{
        String uniqueFileName = directory + "/"  + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + uniqueFileName;
    }
}
