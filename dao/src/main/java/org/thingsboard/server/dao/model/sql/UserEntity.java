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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/21/2017.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_PG_HIBERNATE_COLUMN_FAMILY_NAME)
public class UserEntity extends BaseSqlEntity<User> implements SearchTextEntity<User> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Column(name = ModelConstants.USER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.USER_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_AUTHORITY_PROPERTY)
    private Authority authority;

    @Column(name = ModelConstants.USER_EMAIL_PROPERTY, unique = true)
    private String email;

    @Column(name = ModelConstants.USER_PHONE_PROPERTY, unique = true)
    private String phone;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = ModelConstants.USER_FIRST_NAME_PROPERTY)
    private String firstName;

    @Column(name = ModelConstants.USER_LAST_NAME_PROPERTY)
    private String lastName;

    @Type(type = "json")
    @Column(name = ModelConstants.USER_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    @Column(name = ModelConstants.USER_ROLE_ID_PROPERTY)
    private UUID roleId;

    @Column(name = ModelConstants.USER_AVATAR_PROPERTY)
    private String avatar;

    @Transient
    private String roleTitle;

    public UserEntity() {
    }

    public UserEntity(User user) {
        log.info("Called--------------------------------------------");
        this.setUuid(user.getUuidId());
        log.info("-1--------------------------------------------");
        this.setCreatedTime(user.getCreatedTime());
        log.info("0--------------------------------------------");
        if (user.getTenantId() != null && !user.getTenantId().isNullUid()) {
            this.tenantId = user.getTenantId().getId();
            log.info("1--------------------------------------------");
        }
        if (user.getCustomerId() != null) {
            this.customerId = user.getCustomerId().getId();
            log.info("2--------------------------------------------");
        }
        this.authority = user.getAuthority();
        log.info("3--------------------------------------------");

        this.email = user.getEmail();
        log.info("4--------------------------------------------");

        this.phone = user.getPhone();
        log.info("5--------------------------------------------");

        this.searchText = user.getSearchText();
        log.info("6--------------------------------------------");

        this.firstName = user.getFirstName();
        log.info("7--------------------------------------------");

        this.lastName = user.getLastName();
        log.info("8--------------------------------------------");

        this.additionalInfo = objectMapper.valueToTree(additionalInfo);
        log.info("9--------------------------------------------");

        if (user.getRoleId() != null && !user.getRoleId().isNullUid()) {
            this.roleId = user.getRoleId().getId();
            log.info("10--------------------------------------------");
            this.roleTitle = user.getRoleTitle();
            log.info("11--------------------------------------------");
        }
        this.avatar = user.getAvatar();
        log.info("12--------------------------------------------");
    }

    public UserEntity(UUID id, UUID tenantId, UUID customerId, Long createdTime, Authority authority,
                      String email, String phone, String searchText, String firstName,
                      String lastName, Object additionalInfo, UUID roleId, String avatar, String roleTitle) {
        this.setUuid(id);
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.authority = authority;
        this.setCreatedTime(createdTime);
        this.email = email;
        this.phone = phone;
        this.searchText = searchText;
        this.firstName = firstName;
        this.lastName = lastName;
        this.additionalInfo = objectMapper.valueToTree(additionalInfo);
        this.roleId = roleId;
        this.avatar = avatar;
        log.info("Role Title: " + roleTitle);
        this.roleTitle = roleTitle;
        log.info("Role Title: " + this.roleTitle);

    }

    @Override
    public String getSearchTextSource() {
        return phone;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public User toData() {
        User user = new User(new UserId(this.getUuid()));
        user.setCreatedTime(createdTime);
        user.setAuthority(authority);
        if (tenantId != null) {
            user.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            user.setCustomerId(new CustomerId(customerId));
        }
        user.setEmail(email);
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdditionalInfo(additionalInfo);
        if (roleId != null) {
            user.setRoleId(new RoleId(roleId));
            user.setRoleTitle(roleTitle);
        }
        log.info("Role Title: " + user.getRoleTitle());
        user.setAvatar(avatar);
        return user;
    }

}
