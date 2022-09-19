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

package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.ROLE_COLUMN_FAMILY_NAME)
public class RoleEntity extends BaseSqlEntity<Role> implements SearchTextEntity<Role> {

    @Column(name = ModelConstants.ROLE_TILE_PROPERTY)
    private String title;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Type(type = "json")
    @Column(name = ModelConstants.ROLE_PERMISSIONS_PROPERTY)
    private JsonNode permissions;

    @Column(name = ModelConstants.ROLE_TENANT_ID_PROPERTY)
    private UUID tenantId;

    public RoleEntity() {}

    public RoleEntity(Role role) {
        if (role.getId() != null) {
            this.setUuid(role.getId().getId());
        }
        this.title = role.getTitle();
        this.permissions = role.getPermissions();
        this.tenantId = role.getTenantId().getId();
    }

    @Override
    public String getSearchTextSource() {
        return this.title;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public Role toData() {
        Role role = new Role(new RoleId(this.getUuid()));
        role.setCreatedTime(createdTime);
        role.setTitle(title);
        role.setPermissions(permissions);
        role.setTenantId(TenantId.fromUUID(tenantId));
        return role;
    }
}
