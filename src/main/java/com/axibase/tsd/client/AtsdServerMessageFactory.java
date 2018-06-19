package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;

/**
 * Extract message from Server error message.
 */
final class AtsdServerMessageFactory {
    private AtsdServerMessageFactory() {

    }

    private static final String AUTH_ERROR_PATTERN = "^code \\d{2}$";

    private static final String[] AUTH_MESSAGES = {
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
    private static final int CODE_START_POSITION = 5;


    static String from(final ServerError serverError) {
        final String message = serverError.getMessage();
        if (message != null && message.matches(AUTH_ERROR_PATTERN)) {
            final int code = Integer.parseInt(message.substring(CODE_START_POSITION));
            if (code < 1 || code > AUTH_MESSAGES.length) {
                return message;
            }
            return AUTH_MESSAGES[code - 1];
        }
        return message;
    }
}
