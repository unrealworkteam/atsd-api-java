package com.axibase.tsd.util;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
public class AtsdUtil {
    public static final String JSON = MediaType.APPLICATION_JSON;
    public static final String ADD_COMMAND = "add";
    public static final String DELETE_COMMAND = "delete";

    public static Map<String, String> toMap(String[] tagNamesAndValues) {
        if (tagNamesAndValues==null || tagNamesAndValues.length==0) {
            return Collections.emptyMap();
        }

        if (tagNamesAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("Key without value");
        }

        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < tagNamesAndValues.length; i++) {
            result.put(tagNamesAndValues[i],tagNamesAndValues[++i]);
        }
        return result;
    }

    public static void check(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
