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
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.io.IOException;
import java.io.InputStream;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class User extends SearchTextBasedWithAdditionalInfo<UserId> implements HasName, HasTenantId, HasCustomerId {

    private static final long serialVersionUID = 8250339805336035966L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String email;
    private String phone;
    private Authority authority;
    @NoXss
    @Length(fieldName = "first name")
    private String firstName;
    @NoXss
    @Length(fieldName = "last name")
    private String lastName;

    private RoleId roleId;

    @ApiModelProperty(position = 13, value = "Title of user role", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String roleTitle;

    @Length(fieldName = "avatar", max = 1000000)
    private String avatar;

    public User() {
        super();
    }

    public User(UserId id) {
        super(id);
    }

    public User(User user) {
        super(user);
        this.tenantId = user.getTenantId();
        this.customerId = user.getCustomerId();
        this.email = user.getEmail();
        this.authority = user.getAuthority();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.roleId = user.getRoleId();
        this.roleTitle = user.getRoleTitle();
        this.avatar = user.getAvatar();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the User Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer." )
    @Override
    public UserId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the user creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with the Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, value = "JSON object with the Customer Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @ApiModelProperty(position = 5, required = false, value = "Email of the user", example = "user@example.com")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ApiModelProperty(position = 11, required = true, value = "Phone number", example = "+1(415)777-7777")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ApiModelProperty(position = 6, accessMode = ApiModelProperty.AccessMode.READ_ONLY, value = "Duplicates the email of the user, readonly", example = "user@example.com")
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return email;
    }

    @ApiModelProperty(position = 7, required = true, value = "Authority", example = "SYS_ADMIN, TENANT_ADMIN or CUSTOMER_USER")
    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    @ApiModelProperty(position = 8, required = true, value = "First name of the user", example = "John")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ApiModelProperty(position = 9, required = true, value = "Last name of the user", example = "Doe")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ApiModelProperty(position = 10, value = "Additional parameters of the user", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public String getSearchText() {
        return getPhone();
    }

    @ApiModelProperty(position = 12, value = "JSON object with Role Id")
    public RoleId getRoleId() { return roleId; }

    @ApiModelProperty(position = 13, value = "Title of user role")
    public String getRoleTitle() { return roleTitle; }

    public void setRoleId(RoleId roleId) { this.roleId = roleId;}

    private String getDefaultAvatar() {
        String defaultAvatar = "";
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("img/avatar.png");
            byte[] avatarBytes = IOUtils.toByteArray(is);
            defaultAvatar = "data:image/png;base64,".concat(Base64Utils.encodeToString(avatarBytes));
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Default avatar is not found or its size is too large");
        }
        return defaultAvatar;
    }

    @ApiModelProperty(position = 14, value = "Either URL or Base64 data of the avatar")
    public String getAvatar() {
        if (avatar == null || avatar.length() == 0) {
            return getDefaultAvatar();
        }
        return avatar;
    }
    public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle;}

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", email=");
        builder.append(email);
        builder.append(", authority=");
        builder.append(authority);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", roleId=");
        builder.append(roleId);
        builder.append(", roleTitle=");
        builder.append(roleTitle);
        builder.append(", avatar=");
        builder.append(avatar);
        builder.append("]");
        return builder.toString();
    }

    @JsonIgnore
    public boolean isSystemAdmin() {
        return tenantId == null || EntityId.NULL_UUID.equals(tenantId.getId());
    }

    @JsonIgnore
    public boolean isTenantAdmin() {
        return !isSystemAdmin() && (customerId == null || EntityId.NULL_UUID.equals(customerId.getId()));
    }

    @JsonIgnore
    public boolean isCustomerUser() {
        return !isSystemAdmin() && !isTenantAdmin();
    }
}
