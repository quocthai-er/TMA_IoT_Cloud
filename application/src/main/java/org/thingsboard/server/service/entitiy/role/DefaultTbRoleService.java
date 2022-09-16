package org.thingsboard.server.service.entitiy.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;

@Service
@AllArgsConstructor
public class DefaultTbRoleService extends AbstractTbEntityService implements TbRoleService {

    private final RoleService roleService;

    @Override
    public Role save(Role role) throws Exception {
        ActionType actionType = role.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            Role savedRole = checkNotNull(roleService.saveRole(role));
//            notificationEntityService.noti
        } catch (Exception e) {
//            notificationEntityService.logEntityAction();
        }
        return null;
    }

    @Override
    public void delete(Role role) throws Exception {

    }
}
