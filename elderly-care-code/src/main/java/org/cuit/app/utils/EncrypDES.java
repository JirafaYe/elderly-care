package org.cuit.app.utils;

import org.apache.commons.codec.binary.Hex;
import org.cuit.app.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密
 */
public class EncrypDES {

    /**
     * 加密盐
     */
    private static final String SLAT = "SALT_ElderlyCare2022@#$%^&";

    public static String encrypt(String password){
        MessageDigest messageDigest;
        String str = password+SLAT;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = Hex.encodeHexString(messageDigest.digest());

        } catch (NoSuchAlgorithmException e) {
            throw new AppException("未知错误,请重试");
        }

        return encodeStr;
    }

}