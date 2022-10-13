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

package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.role.TbRoleService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.Arrays;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;

@Slf4j
@RestController
@TbCoreComponent
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController extends BaseController{

    public static final String ROLE_ID = "roleId";
    public static final String USER_ID = "userId";

    private final TbRoleService tbRoleService;

    @ApiOperation(value = "Get Role (getRoleById)",
            notes = "Fetch the User object based on the provided Role Id. " +
                    "Role Contains a set of permissions. ")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/role/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public Role getRoleById(
            @ApiParam(value = ROLE_ID_PARAM_DESCRIPTION)
            @PathVariable(ROLE_ID) String strRoleId) throws ThingsboardException {
        checkParameter(ROLE_ID, strRoleId);
        try {
            RoleId roleId = new RoleId(toUUID(strRoleId));
            Role role = checkRoleId(roleId, Operation.READ);
            return role;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Check current (customer) user permission (checkPermission) TEST-ONLY")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/role/check-permission", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkPermission(
            @ApiParam(value = "Resource")
            @RequestParam String strResource,
            @ApiParam(value = "Operation")
            @RequestParam String strOperation) throws ThingsboardException {
        try {
            //RoleId roleId = new RoleId(toUUID(strRoleId));
            return checkPermission(getCurrentUser().getId(), strResource, strOperation);
        }
        catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Create or update Role (saveRole)",
            notes =  "Creates or Updates the Role. ")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public Role saveRole(
            @ApiParam(value = "A JSON value representing the role.")
            @RequestBody Role role) throws Exception {
        role.setTenantId(getTenantId());
        checkEntity(role.getId(), role, Resource.ROLE);
        return tbRoleService.save(role);
    }

    @ApiOperation(value = "Get Roles (getRoles)",
            notes = "Returns a page of roles owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/roles", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Role> getRoles(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = ROLE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ROLE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(roleService.findRolesByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Delete Role (deleteRole)",
            notes = "Deletes the Role with specific ID. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/role/{roleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteRole(@ApiParam(value = ROLE_ID_PARAM_DESCRIPTION)
                               @PathVariable(ROLE_ID) String strRoleId) throws Exception {
        checkParameter(ROLE_ID, strRoleId);
        RoleId roleId = new RoleId(toUUID(strRoleId));
        Role role = checkRoleId(roleId, Operation.DELETE);
        tbRoleService.delete(role);
    }

    private boolean checkPermission(UserId userId, String resource, String operation) {
        Role role = roleService.findRoleByUserId(userId);
        if (role == null || role.getPermissions().isMissingNode()) {
            return false;
        }
        boolean hasPermission = false;
        boolean hasAllPermissions = false;
        if (!role.getPermissions().path(resource).isMissingNode()) {
            ArrayNode singleResourcePermissions = (ArrayNode) role.getPermissions().path(resource);
            hasPermission = isOperationContains(singleResourcePermissions, operation);
            log.info("Single:" + singleResourcePermissions.asText());
        }
        if (hasPermission == false && !role.getPermissions().path("ALL").isMissingNode()) {
            ArrayNode allResourcesPermissions = (ArrayNode) role.getPermissions().path("ALL");
            hasAllPermissions = isOperationContains(allResourcesPermissions, operation);
            log.info("All: " + allResourcesPermissions.asText());
        }
        return hasPermission || hasAllPermissions;
    }

    private boolean isOperationContains(ArrayNode permissions, String operation) {
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
