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

package org.thingsboard.server.dao.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.RoleId;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleService roleService;

    @Override
    public Role findRoleById(RoleId roleId) {
//        log.trace("Executing findRoleById [{}]", roleId);
        return roleDao.findByRoleId(roleId.getId());
    }

//    @Override
//    public Optional<Role> findRoleByTitle(RoleId roleId, String title) {
//        return Optional.empty();
//    }

    @Override
    public Role saveRole(Role role) {
        try {
            Role savedRole = roleDao.save(role.getTenantId(), role);
            return savedRole;
        } catch (Exception e) {
            throw e;
        }
    }

//    @Override
//    public void deleteRole(RoleId roleId) {
//
//    }
}
