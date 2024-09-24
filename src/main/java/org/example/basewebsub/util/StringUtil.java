package org.example.basewebsub.util;

import java.sql.Timestamp;
import java.text.*;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  hieunt
 */
public class StringUtil {


    public static boolean isNullOrEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public static boolean isNotNullAndEmpty(String str) {
        return (!(isNullOrEmpty(str)));
    }

    public static boolean isBlank(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (!(Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return (!(isBlank(str)));
    }

    public static boolean equals(String str1, String str2) {
        return (str1 != null && (str2 == null || str1.equals(str2)));
    }

}
