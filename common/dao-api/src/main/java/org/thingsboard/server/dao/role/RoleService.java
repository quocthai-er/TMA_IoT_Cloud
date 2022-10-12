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
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.Optional;

public interface RoleService {

    Role findRoleById(RoleId roleId);

//    Optional<Role> findRoleByTitle(RoleId roleId, String title);

    Role saveRole(Role role);

//    void deleteRole(RoleId roleId);
    PageData<Role> findRolesByTenantId(TenantId tenantId, PageLink pageLink);

    void deleteRole(RoleId roleId);

    ListenableFuture<Role> findRoleByIdAsync(TenantId tenantId, RoleId roleId);

//    Role findRoleByCustomerId(CustomerId customerId);

    Role findRoleByUserId(UserId userId);

    Role findByRoleTitle(String title);
    Role findRoleByTenantIdAndTitle(TenantId tenantId, String title);

}
