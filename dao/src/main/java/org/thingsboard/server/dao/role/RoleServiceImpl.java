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

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    public static final String INCORRECT_ROLE_ID = "Incorrect roleId ";
    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DataValidator<Role> roleValidator;

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
        log.trace("Executing saveRole [{}]", role);
        roleValidator.validate(role, Role::getTenantId);
        try {
            Role savedRole = roleDao.save(role.getTenantId(), role);
            return savedRole;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PageData<Role> findRolesByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findRolesByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validatePageLink(pageLink);
        return roleDao.findRolesByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public void deleteRole(RoleId roleId) {
        log.trace("Executing deleteRole [{}]", roleId);
        Validator.validateId(roleId, INCORRECT_ROLE_ID + roleId);
        Role role = findRoleById(roleId);
        if (role == null) {
            throw new IncorrectParameterException("Unable to delete non-existent role.");
        }
        roleDao.removeById(null, roleId.getId());
    }

    @Override
    public ListenableFuture<Role> findRoleByIdAsync(TenantId tenantId, RoleId roleId) {
        log.trace("Executing findRoleByIdAsync [{}]", roleId);
        validateId(roleId, INCORRECT_ROLE_ID + roleId);
        return roleDao.findByIdAsync(tenantId, roleId.getId());
    }

//    @Override
//    public Role findRoleByCustomerId(CustomerId customerId) {
//        log.info("Executing findRoleByCustomerId [{}]", customerId);
//        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
//        return roleDao.findByCustomerId(customerId.getId());
//    }

    @Override
    public Role findRoleByUserId(UserId userId) {
        log.info("Executing findRoleByUserId [{}]", userId);
        validateId(userId, INCORRECT_CUSTOMER_ID + userId);
        return roleDao.findByUserId(userId.getId());
    }

    @Override
    public Role findByRoleTitle(String title) {
        log.info("Executing findRoleByTitle [{}]", title);
        return roleDao.findByRoleTitle(title);
    }

    @Override
    public Role findRoleByTenantIdAndTitle(TenantId tenantId, String title) {
        log.trace("Executing findRoleByTenantIdAndTitle [{}] [{}]", tenantId, title);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return roleDao.findRoleByTenantIdAndTitle(tenantId.getId(), title);
    }
}
