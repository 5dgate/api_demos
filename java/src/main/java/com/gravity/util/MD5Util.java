package com.gravity.util;

import java.security.MessageDigest;

/**
 * Copyright (C), 2017, 黑曜石
 *
 * @author jimmy
 * @desc MD5Util
 * @date 2017/9/28
 */
public class MD5Util {
    public static String encrypt(String string) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] bytes = string.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            byte[] updateBytes = messageDigest.digest();
            int len = updateBytes.length;
            char myChar[] = new char[len * 2];
            int k = 0;
            for (byte b : updateBytes) {
                myChar[k++] = hexDigits[b >>> 4 & 0x0f];
                myChar[k++] = hexDigits[b & 0x0f];
            }
            return new String(myChar);
        } catch (Exception e) {
            return null;
        }
    }
}

