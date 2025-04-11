package edu.zhou.quantflow.util;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @Author Righter
 * @Description
 * @Date since 3/13/2025
 */
@Log4j2
public class HashUtil {
    /*
    * 获取12位哈希值
    * */
    public static String getHash12(InputStream inputStream){
        StringBuilder hexString = new StringBuilder();
        try{
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = inputStream.readAllBytes();
            byte[] hashBytes = digest.digest(fileBytes);

            for(byte b : hashBytes){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');// 例如字节0x0f, 转换为hexString为f, 需要补0为0f
                hexString.append(hex);
            }

        }catch (Exception e){
            log.error("获取hash值失败! "+ e.getMessage());
        }
        return hexString.substring(0,12);
    }
}
