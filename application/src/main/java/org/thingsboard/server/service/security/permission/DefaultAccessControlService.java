/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.security.permission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.*;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
public class DefaultAccessControlService implements AccessControlService {

    private static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    private static final String YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION = "You don't have permission to perform this operation!";

    private final Map<Authority, Permissions> authorityPermissions = new HashMap<>();

    @Autowired
    private RoleService roleService;

    public DefaultAccessControlService(
            @Qualifier("sysAdminPermissions") Permissions sysAdminPermissions,
            @Qualifier("tenantAdminPermissions") Permissions tenantAdminPermissions,
            @Qualifier("customerUserPermissions") Permissions customerUserPermissions) {
        authorityPermissions.put(Authority.SYS_ADMIN, sysAdminPermissions);
        authorityPermissions.put(Authority.TENANT_ADMIN, tenantAdminPermissions);
        authorityPermissions.put(Authority.CUSTOMER_USER, customerUserPermissions);
    }

    @Override
    public void checkPermission(SecurityUser user, Resource resource, Operation operation) throws ThingsboardException {
        if (user.getAuthority() == Authority.CUSTOMER_USER && !canAccess(user, resource.toString(), operation.toString())) {
            permissionDenied();
        }
        PermissionChecker permissionChecker = getPermissionChecker(user.getAuthority(), resource);
        if (!permissionChecker.hasPermission(user, operation)) {
            permissionDenied();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends EntityId, T extends HasTenantId> void checkPermission(SecurityUser user, Resource resource,
                                                                                            Operation operation, I entityId, T entity) throws ThingsboardException {
        if (user.getAuthority() == Authority.CUSTOMER_USER && !canAccess(user, resource.toString(), operation.toString())) {
            permissionDenied();
        }
        PermissionChecker permissionChecker = getPermissionChecker(user.getAuthority(), resource);
        if (!permissionChecker.hasPermission(user, operation, entityId, entity)) {
            permissionDenied();
        }
    }

    private PermissionChecker getPermissionChecker(Authority authority, Resource resource) throws ThingsboardException {
        Permissions permissions = authorityPermissions.get(authority);
        if (permissions == null) {
            permissionDenied();
        }
        Optional<PermissionChecker> permissionChecker = permissions.getPermissionChecker(resource);
        if (!permissionChecker.isPresent()) {
            permissionDenied();
        }
        return permissionChecker.get();
    }

    private void permissionDenied() throws ThingsboardException {
        throw new ThingsboardException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION,
                ThingsboardErrorCode.PERMISSION_DENIED);
    }

    private boolean canAccess(SecurityUser user, String resource, String operation) {
        boolean hasPermission = false;
        boolean hasAllPermissions = false;
        Role role = roleService.findRoleByCustomerId(user.getCustomerId());
        if (role == null || role.getPermissions().isMissingNode()) {
            return false;
        }
        if (!role.getPermissions().path(resource).isMissingNode()) {
            ArrayNode singleResourcePermissions = (ArrayNode) role.getPermissions().path(resource);
            hasPermission = isOperationContained(singleResourcePermissions, operation);
            log.info("Single:" + singleResourcePermissions.asText());
        }
        if (hasPermission == false && !role.getPermissions().path("ALL").isMissingNode()) {
            ArrayNode allResourcesPermissions = (ArrayNode) role.getPermissions().path("ALL");
            hasAllPermissions = isOperationContained(allResourcesPermissions, operation);
            log.info("All: " + allResourcesPermissions.asText());
        }
        return hasPermission || hasAllPermissions;
    }

    private boolean isOperationContained(ArrayNode permissions, String operation) {
        if (permissions != null && permissions.isArray()) {
            if (permissions.get(0).asText().equals("ALL")) {
                return true;
            }
            for (JsonNode permission : permissions) {
                if (permission.asText().equals(operation)) {
                    return true;
                }
            }
        }
        return false;
    }

}
