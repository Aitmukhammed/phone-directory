package com.company.phone_directory.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validators {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PHONE_REGEX = "^(?:\\+7|8)\\s?7\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}$";

    public static boolean emailIsValid(String email) {
        if(email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static String phoneFormatter(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }

        if (phoneNumber.matches(".*[a-zA-Zа-яА-Я].*")) {
            throw new IllegalArgumentException("Phone number should not contain letters.");
        }

        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

        if (phoneNumber.startsWith("8")) {
            phoneNumber = "+7" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+7")) {
            phoneNumber = "+" + phoneNumber;
        }

        return phoneNumber;
    }


}
