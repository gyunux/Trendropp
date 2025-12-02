package com.celebstyle.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Value
@Slf4j
public class S3UploadService {
    private final S3Client s3Client;
    private final String bucketName = "celebstyletest-storage";

    @Autowired
    public S3UploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file, String directory) throws IOException {
        String uniqueFileName = directory + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + uniqueFileName;
    }

    public String uploadFromUrl(String imageUrl, String directory) throws IOException {
        String uniqueFileName = directory + "/" + UUID.randomUUID().toString() + ".jpg";

        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType("image/jpeg") // 이미지 타입 지정
                    .build();
            byte[] imagesBytes = in.readAllBytes();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imagesBytes));

            // 5. 업로드된 파일의 최종 S3 URL을 반환합니다.
            return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + uniqueFileName;
        }
    }

    public void deleteImage(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String splitStr = ".amazonaws.com/";
            int index = fileUrl.indexOf(splitStr);

            if (index == -1) {
                log.warn("S3 URL 형식이 아닙니다: {}", fileUrl);
                return;
            }

            String fileName = fileUrl.substring(index + splitStr.length());

            String decodedKey = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(decodedKey)
                    .build());
            log.info("S3 이미지 삭제 완료: {}", decodedKey);
        } catch (Exception e) {
            log.error("S3 이미지 삭제 중 오류 발생: {}", fileUrl, e);
        }
    }
}
