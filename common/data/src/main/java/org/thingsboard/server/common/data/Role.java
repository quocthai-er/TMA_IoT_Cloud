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

package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;

public class Role extends BaseData<RoleId> implements HasTenantId {

    @ApiModelProperty(position = 5, value = "JSON object with Tenant Id")
    private TenantId tenantId;

    @Length(fieldName = "title")
    private String title;

    @Length(fieldName = "operations")
    private transient JsonNode operations;

    public Role() {
        super();
    }

    public Role(RoleId id) {
        super(id);
    }

    public Role(Role role) {
        super(role);
        this.tenantId = role.getTenantId();
        this.title = role.getTitle();
        this.operations = role.getOperations();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Role Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer." )
    @Override
    public RoleId getId() { return super.getId(); }

    @ApiModelProperty(position = 2, value = "Timestamp of the role creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "Title of the role", example = "MANAGER")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(position = 4, value = "List operations of role", dataType = "com.fasterxml.jackson.databind.JsonNode")
    public JsonNode getOperations() {
        return this.operations;
    }

    public void setOperations(JsonNode operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [title=");
        builder.append(getTitle());
        builder.append(", operations=");
        builder.append(operations);
        builder.append("]");
        return builder.toString();
    }

}
