package com.it.reggie.utils;

import java.util.Random;

/**
 * Utility class for generating random verification codes
 */
public class ValidateCodeUtils {
    /**
     * Generate a random verification code
     * @param length Length of 4 or 6 digits
     * @return Verification code
     */
    public static Integer generateValidateCode(int length) {
        Integer code = null;
        if (length == 4) {
            code = new Random().nextInt(9999); // Generate a random number, maximum 9999
            if (code < 1000) {
                code = code + 1000; // Ensure the random number is 4 digits
            }
        } else if (length == 6) {
            code = new Random().nextInt(999999); // Generate a random number, maximum 999999
            if (code < 100000) {
                code = code + 100000; // Ensure the random number is 6 digits
            }
        } else {
            throw new RuntimeException("Can only generate 4 or 6 digit verification codes");
        }
        return code;
    }

    /**
     * Generate a random string verification code of specified length
     * @param length Length of the string
     * @return Verification code
     */
    public static String generateValidateCode4String(int length) {
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        String capstr = hash1.substring(0, length);
        return capstr;
    }
}
