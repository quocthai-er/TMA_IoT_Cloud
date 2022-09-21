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

package org.thingsboard.server.dao.sql.role;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.RoleEntity;
import org.thingsboard.server.dao.role.RoleDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRoleDao extends JpaAbstractSearchTextDao<RoleEntity, Role> implements RoleDao {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected Class<RoleEntity> getEntityClass() { return RoleEntity.class; }

    @Override
    protected JpaRepository<RoleEntity, UUID> getRepository() { return roleRepository; }

    @Override
    public Role findByRoleId(UUID roleId) {
        Role role = DaoUtil.getData(roleRepository.findById(roleId));
        return role;
    }

    @Override
    public Optional<Role> findRolesByTenantIdAndTitle(UUID tenantId, String title) {
        Role role = DaoUtil.getData(roleRepository.findByTenantIdAndTitle(tenantId, title));
        return Optional.ofNullable(role);
    }

    @Override
    public EntityType getEntityType() { return EntityType.ROLE; }

    @Override
    public PageData<Role> findRolesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(roleRepository.findByTenantId(
                tenantId,
                Objects.toString(pageLink.getTextSearch(), ""),
                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Role findByCustomerId(UUID customerId) {
        Role role = DaoUtil.getData(roleRepository.findByCustomerId(customerId));
        return role;
    }
}
