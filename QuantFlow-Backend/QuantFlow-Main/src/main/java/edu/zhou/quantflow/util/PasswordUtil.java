package edu.zhou.quantflow.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author Righter
 * @Description
 * @Date since 3/5/2025
 */
public class PasswordUtil {
    private static final  BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static String encode(String password) {
        return PASSWORD_ENCODER.encode(password);
    }
    public static boolean matches(String password, String encodedPassword) {
        return PASSWORD_ENCODER.matches(password, encodedPassword);
    }
}
