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
package org.thingsboard.server.dao.sql.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.sql.UserEntity;

import java.util.UUID;

/**
 * @author Valerii Sosliuk
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmail(String email);

    UserEntity findByPhone(String phone);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.UserEntity(u.id, u.tenantId, u.customerId, u.createdTime, u.authority, u.email, u.phone, u.searchText, u.firstName, u.lastName, u.additionalInfo, u.roleId, u.avatar, r.title) FROM UserEntity u " +
            "LEFT JOIN RoleEntity r on r.id = u.roleId " +
            "WHERE u.tenantId = :tenantId " +
            "AND u.customerId = :customerId AND u.authority = :authority " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<UserEntity> findUsersByAuthority(@Param("tenantId") UUID tenantId,
                                          @Param("customerId") UUID customerId,
                                          @Param("searchText") String searchText,
                                          @Param("authority") Authority authority,
                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.UserEntity(u.id, u.tenantId, u.customerId, u.createdTime, u.authority, u.email, u.phone, u.searchText, u.firstName, u.lastName, u.additionalInfo, u.roleId, r.title)" +
            " FROM UserEntity u " +
            "LEFT JOIN RoleEntity r on r.id = u.roleId " +
            "WHERE u.tenantId = :tenantId " +
            "AND u.customerId = :customerId AND u.authority = :authority " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<UserEntity> findUsersByAuthorityNotAvatar(@Param("tenantId") UUID tenantId,
                                          @Param("customerId") UUID customerId,
                                          @Param("searchText") String searchText,
                                          @Param("authority") Authority authority,
                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.UserEntity(u.id, u.tenantId, u.customerId, u.createdTime, u.authority, u.email, u.phone, u.searchText, u.firstName, u.lastName, u.additionalInfo, u.roleId, u.avatar, r.title) FROM UserEntity u " +
            "LEFT JOIN RoleEntity r on r.id = u.roleId " +
            "WHERE u.tenantId = :tenantId " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<UserEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                    @Param("searchText") String searchText,
                                    Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.UserEntity(u.id, u.tenantId, u.customerId, u.createdTime, u.authority, u.email, u.phone, u.searchText, u.firstName, u.lastName, u.additionalInfo, u.roleId, r.title) " +
            "FROM UserEntity u " +
            "LEFT JOIN RoleEntity r on r.id = u.roleId " +
            "WHERE u.tenantId = :tenantId " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<UserEntity> findByTenantIdNotAvatar(@Param("tenantId") UUID tenantId,
                                    @Param("searchText") String searchText,
                                    Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.UserEntity(u.id, u.tenantId, u.customerId, u.createdTime, u.authority, u.email, u.phone, u.searchText, u.firstName, u.lastName, u.additionalInfo, u.roleId, r.title) " +
            "FROM UserEntity u " +
            "LEFT JOIN RoleEntity r on r.id = u.roleId " +
            "WHERE u.id = :userId")
    UserEntity findByIdNotAvatar(@Param("userId") UUID userId);

    Long countByTenantId(UUID tenantId);
}
