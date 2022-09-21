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

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.Optional;
import java.util.UUID;

public interface RoleDao extends Dao<Role> {
    /**
     * Save or update role object
     *
     * @param role the role object
     * @return saved role object
     */
    Role save(TenantId tenantId, Role role);

    /**
     * Find roles by roleId and role title.
     *
     * @param roleId the roleId
     * @return the optional role object
     */
    Role findByRoleId(UUID roleId);

    /**
     * Find roles by tenantId and role title.
     *
     * @param tenantId the tenantId
     * @param title the role title
     * @return the optional role object
     */
    Optional<Role> findRolesByTenantIdAndTitle(UUID tenantId, String title);

    /**
     * Find roles by tenant id and page link.
     *
     * @param tenantId the tenant id
     * @param pageLink the page link
     * @return the list of role objects
     */
    PageData<Role> findRolesByTenantId(UUID tenantId, PageLink pageLink);


    /**
     * Find roles by customer id.
     *
     * @param customerId the customer id
     * @return the optional role object
     */
    Role findByCustomerId(UUID customerId);
}
