package com.axibase.tsd.client;


import com.axibase.tsd.model.permissions.AccessPermission;
import com.axibase.tsd.query.Query;
import lombok.RequiredArgsConstructor;

/**
 * Contains extended REST API methods wrappers.
 *
 * @see <a href="https://axibase.com/docs/atsd/api/data/#misc"></a>
 */
@RequiredArgsConstructor(staticName = "with")
public class ExtendedService {
    private final HttpClientManager httpClientManager;

    /**
     * Retrieves access permissions for the current user..
     *
     * @return AccessPermission instance
     * @see <a href="https://axibase.com/docs/atsd/api/meta/misc/permissions.html">Permissions</a>
     */
    public AccessPermission permissions() {
        return httpClientManager.requestData(
                AccessPermission.class,
                new Query<AccessPermission>("permissions"),
                null);
    }
}
