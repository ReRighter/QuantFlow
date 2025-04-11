package edu.zhou.quantflow;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author Righter
 * @Description
 * @Date since 3/11/2025
 */
public class MinioTest {
    static MinioClient client = new MinioClient.Builder()
            .endpoint("http://110.41.47.134:9000")
            .credentials("minioadmin","minioadmin")
            .build();
    @Test
    public void TestAdd()  {
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    .bucket("test")
                    .object("/image/testImage.png")
                    .filename("C:\\Users\\Righter\\Downloads\\cover金融学基础.jpg")
                    .contentType("image/png").build();
            client.uploadObject(args);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void TestPreSignedURL(){
        try {
        String url =  client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .bucket("test")
                        .object("image/1.1.1.1 金融股的概念.mp4")
                        .method(Method.GET)
                .build());
            System.out.println("url: "+url);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void getFileHash() throws NoSuchAlgorithmException {
        try(FileInputStream fileInputStream =
                    new FileInputStream("D:\\school\\视频资源\\金融学基础\\【国家级精品课】《金融学基础》(全40讲)了解金融的本质，掌握资本市场的规律！\\1.1.1.1 金融股的概念.mp4")){

            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileInputStream.readAllBytes();
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder hexString = new StringBuilder();
            for(byte b : hashBytes){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');// 例如字节0x0f, 转换为hexString为f, 需要补0为0f
                hexString.append(hex);
            }
            System.out.println(hexString);
        }catch (Exception e){
            System.out.println(e.getMessage());

        }

    }
    @Test
    public void testContentType() throws IOException {
        System.out.println( Files.probeContentType(Path.of("123.jpg")));
    }

    public static void main(String[] args) {
        byte b = 0x0f;
        String hex = Integer.toHexString(0xff & b);
        System.out.println(hex);
    }
}
