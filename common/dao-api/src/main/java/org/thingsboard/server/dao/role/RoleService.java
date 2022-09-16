package org.thingsboard.server.dao.role;

import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.RoleId;

import java.util.Optional;

public interface RoleService {

    Role findRoleById(RoleId roleId);

    Optional<Role> findRoleByTitle(RoleId roleId, String title);

    Role saveRole(Role role);

    void deleteRole(RoleId roleId);
}
