package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Extract message from Server error message.
 */
final class AtsdServerMessageFactory {
    private AtsdServerMessageFactory() {

    }

    private static final Map<String, String> AUTH_DICT = authDictionary();


    static String from(final ServerError serverError) {
        final String message = serverError.getMessage();
        return StringUtils.defaultString(AUTH_DICT.get(message), message);
    }

    private static Map<String, String> authDictionary() {
        final Map<String, String> dict = new HashMap<>();
        dict.put("code 01", "General Server Error");
        dict.put("code 02", "Username Not Found");
        dict.put("code 03", "Bad Credentials");
        dict.put("code 04", "Disabled LDAP Service");
        dict.put("code 05", "Corrupted Configuration");
        dict.put("code 06", "MS Active Directory");
        dict.put("code 07", "Account Disabled");
        dict.put("code 08", "Account Expired");
        dict.put("code 09", "Account Locked");
        dict.put("code 10", "Logon Not Permitted At Time");
        dict.put("code 11", "Logon Not Permitted At Workstation");
        dict.put("code 12", "Password Expired");
        dict.put("code 13", "Password Reset Required");
        dict.put("code 14", "Wrong IP Address");
        dict.put("code 15", "Access Denied");
        return Collections.unmodifiableMap(dict);
    }
}
