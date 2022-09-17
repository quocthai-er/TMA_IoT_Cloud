package org.thingsboard.server.service.entitiy.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;

@Service
@AllArgsConstructor
public class DefaultTbRoleService extends AbstractTbEntityService implements TbRoleService {

    private final RoleService roleService;

    @Override
    public Role save(Role role, Role oldRole) throws Exception {
        ActionType actionType = role.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            Role savedRole = checkNotNull(roleService.saveRole(role));
            notificationEntityService.notifyCreateOrUpdateRole(role, oldRole, actionType);
            return savedRole;
        } catch (Exception e) {
//            notificationEntityService.logEntityAction();
            throw e;
        }
    }

    @Override
    public void delete(Role role) throws Exception {
        RoleId roleId = role.getId();
        try {
            roleService.deleteRole(roleId);
            notificationEntityService.notifyDeleteRole(role);
        } catch (Exception e) {
//            notificationEntityService.logEntityAction();
            throw e;
        }
    }
}
