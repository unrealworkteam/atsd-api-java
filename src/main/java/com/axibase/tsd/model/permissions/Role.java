package com.axibase.tsd.model.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * All available USER and API permission roles.
 *
 * @see <a href="https://axibase.com/docs/atsd/administration/user-authorization.html#api-roles">API Roles</a>
 * @see <a href="https://axibase.com/docs/atsd/administration/user-authorization.html##user-interface-roles">
 * UI roles</a>
 */
public enum Role {
    API_DATA_READ,
    API_DATA_WRITE,
    API_META_READ,
    API_META_WRITE,
    USER,
    EDITOR,
    ENTITY_GROUP_ADMIN,
    ADMIN;

    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * How deserilize the Role object from JSON.
     *
     * @param value string representation.
     * @return Role instance.
     */
    @JsonCreator
    public static Role forValue(final String value) {
        for (Role role : Role.values()) {
            if (value.startsWith(ROLE_PREFIX)) {
                final String roleName = role.name();
                if (ROLE_PREFIX.length() + roleName.length() == value.length() && value.endsWith(roleName)) {
                    return role;
                }
            } else {
                throw new IllegalArgumentException("Role should starts with ROLE_");
            }
        }
        throw new IllegalArgumentException(String.format("%s - incorrect role value", value));
    }
}
