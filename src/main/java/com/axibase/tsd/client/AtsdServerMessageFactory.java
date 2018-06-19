package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;
import org.apache.commons.lang3.StringUtils;

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
        final String[] authMessages = {
                "General Server Error",
                "Username Not Found",
                "Bad Credentials",
                "Disabled LDAP Service",
                "Corrupted Configuration",
                "MS Active Directory",
                "Account Disabled",
                "Account Expired",
                "Account Locked",
                "Logon Not Permitted At Time",
                "Logon Not Permitted At Workstation",
                "Password Expired",
                "Password Reset Required",
                "Wrong IP Address",
                "Access Denied"
        };
        final Map<String, String> dict = new HashMap<>();
        for (int i = 0; i < authMessages.length; i++) {
            dict.put(String.format("code %02d", i + 1), authMessages[i]);
        }
        return dict;
    }
}
