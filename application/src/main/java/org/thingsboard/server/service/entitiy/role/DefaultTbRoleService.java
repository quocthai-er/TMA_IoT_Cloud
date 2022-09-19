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

package org.thingsboard.server.service.entitiy.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;

@Service
@AllArgsConstructor
public class DefaultTbRoleService extends AbstractTbEntityService implements TbRoleService {

    private final RoleService roleService;

    @Override
    public Role save(Role role) throws Exception {
        ActionType actionType = role.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            Role savedRole = checkNotNull(roleService.saveRole(role));
            notificationEntityService.notifyCreateOrUpdateRole(role, actionType);
            return savedRole;
        } catch (Exception e) {
//            notificationEntityService.logEntityAction();
            throw e;
        }
    }

//    @Override
//    public void delete(Role role) throws Exception {
//        RoleId roleId = role.getId();
//        try {
//            roleService.deleteRole(roleId);
//            notificationEntityService.notifyDeleteRole(role);
//        } catch (Exception e) {
//            notificationEntityService.logEntityAction();
//            throw e;
//        }
//    }
}
