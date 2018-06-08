package com.axibase.tsd.model.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Access Permission for user. Describes object returned by /api/v1/permission.
 *
 * @see <a href="https://axibase.com/docs/atsd/api/meta/misc/permissions.html#response">Permissions API Response</a>
 */
@Getter
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessPermission {
    private boolean allEntitiesRead;
    private boolean allEntitiesWrite;
    private boolean allPortalsPermission;

    private List<String> userGroups;
    private List<String> portals;
    private Map<String, EntityPermission> entityGroups;
    private List<Role> roles;
    private String ipFilter;

    /**
     * Necessary for serialization and deserialization process with
     * custom {@link PropertyNamingStrategy}.
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public void init() {
        // Necessary for kebab case deserialization
    }
}
