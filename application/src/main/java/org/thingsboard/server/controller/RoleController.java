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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.role.TbRoleService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import javax.servlet.http.HttpServletRequest;

import static org.thingsboard.server.controller.ControllerConstants.ROLE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;

@RestController
@TbCoreComponent
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController extends BaseController{

    public static final String ROLE_ID = "roleId";

    private final TbRoleService tbRoleService;

    @ApiOperation(value = "Get Role (getRoleById)",
            notes = "Fetch the User object based on the provided Role Id. " +
                    "If the user has the authority of 'SYS_ADMIN', the server does not perform additional checks. " +
                    "If the user has the authority of 'TENANT_ADMIN', the server checks that the requested user is owned by the same tenant. " +
                    "If the user has the authority of 'CUSTOMER_USER', the server checks that the requested user is owned by the same customer.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
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

}
