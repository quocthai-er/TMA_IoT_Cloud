package org.thingsboard.server.service.entitiy.role;

import org.thingsboard.server.common.data.Role;

public interface TbRoleService {

    Role save(Role role) throws Exception;

    void delete(Role role) throws Exception;
}
