package com.axibase.tsd.client.extended;

import com.axibase.tsd.TestUtil;
import com.axibase.tsd.client.ExtendedService;
import com.axibase.tsd.client.ServiceFactory;
import com.axibase.tsd.model.permissions.AccessPermission;
import com.axibase.tsd.model.permissions.Role;
import org.junit.Assert;
import org.junit.Test;

public class AccessPermissionTest {
    private ExtendedService extendedService = ServiceFactory
            .with(TestUtil.buildHttpClientManager())
            .extended();

    @Test
    public void testDefaultPermissions() {
        final AccessPermission accessPermission = extendedService.permissions();
        Assert.assertFalse(accessPermission.getRoles().isEmpty());
        Assert.assertTrue(accessPermission.isAllEntitiesRead());
        Assert.assertTrue(accessPermission.isAllEntitiesWrite());
        Assert.assertTrue(accessPermission.isAllPortalsPermission());
        Assert.assertTrue(accessPermission.getRoles().contains(Role.ADMIN));
        Assert.assertTrue(accessPermission.getPortals().isEmpty());
        Assert.assertTrue(accessPermission.getEntityGroups().isEmpty());
    }
}
