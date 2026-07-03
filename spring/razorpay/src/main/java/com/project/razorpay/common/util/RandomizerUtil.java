package com.project.razorpay.common.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class RandomizerUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String randomBase64(int length){

        // UUID.randomUUID().toString().replaceAll("-", "");

        byte[] buf = new byte[length]; // byte range : {-128 , 127}

        SECURE_RANDOM.nextBytes(buf);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
