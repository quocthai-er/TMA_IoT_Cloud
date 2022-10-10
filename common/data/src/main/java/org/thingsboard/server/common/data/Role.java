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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseData<RoleId> implements HasTenantId, HasName {

    public static final ObjectMapper mapper = new ObjectMapper();

    @ApiModelProperty(position = 5, value = "JSON object with Tenant Id")
    private TenantId tenantId;

    @Length(fieldName = "title")
    private String title;

    @Length(fieldName = "label")
    private String label;

    //@Length(fieldName = "permissions")
    private JsonNode permissions;

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
        this.label = role.getLabel();
        this.permissions = role.getPermissions();
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

    @ApiModelProperty(position = 3, value = "Label of the role", example = "MANAGER")
    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    @ApiModelProperty(position = 4, accessMode = ApiModelProperty.AccessMode.READ_ONLY, value = "Duplicates the title of the role, readonly", example = "MANAGER")
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    @JsonIgnore
    private byte[] permissionsBytes;

    @ApiModelProperty(position = 4, value = "List permissions of role", dataType = "com.fasterxml.jackson.databind.JsonNode")
    public JsonNode getPermissions() {
        return getJson(() -> permissions, () -> permissionsBytes);
    }

    public void setPermissions(JsonNode addPermissions) {
        setJson(addPermissions, json -> this.permissions = json, bytes -> this.permissionsBytes = bytes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [title=");
        builder.append(getTitle());
        builder.append(", label=");
        builder.append(getLabel());
        builder.append(", id=");
        builder.append(id);
        builder.append(", permissions=");
        builder.append(permissions);
        builder.append("]");
        return builder.toString();
    }

    public static JsonNode getJson(Supplier<JsonNode> jsonData, Supplier<byte[]> binaryData) {
        JsonNode json = jsonData.get();
        if (json != null) {
            return json;
        } else {
            byte[] data = binaryData.get();
            if (data != null) {
                try {
                    return mapper.readTree(new ByteArrayInputStream(data));
                } catch (IOException e) {
                    log.warn("Can't deserialize json data: ", e);
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static void setJson(JsonNode json, Consumer<JsonNode> jsonConsumer, Consumer<byte[]> bytesConsumer) {
        jsonConsumer.accept(json);
        try {
            bytesConsumer.accept(mapper.writeValueAsBytes(json));
        } catch (JsonProcessingException e) {
            log.warn("Can't serialize json data: ", e);
        }
    }


}
