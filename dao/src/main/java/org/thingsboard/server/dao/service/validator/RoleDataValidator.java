package org.thingsboard.server.dao.service.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.role.RoleDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantService;

import java.util.Optional;

@Component
public class RoleDataValidator extends DataValidator<Role> {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;


    @Override
    protected void validateCreate(TenantId tenantId, Role role) {
//        DefaultTenantProfileConfiguration profileConfiguration =
//                (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
//        long maxRoles = profileConfiguration.getMaxRoles();
//
//        validateNumberOfEntitiesPerTenant(tenantId, roleDao, maxRoles, EntityType.CUSTOMER);
        roleDao.findRolesByTenantIdAndTitle(role.getTenantId().getId(), role.getTitle()).ifPresent(
                r -> {
                    throw new DataValidationException("Role with such title already exists!");
                }
        );
    }

    @Override
    protected Role validateUpdate(TenantId tenantId, Role role) {
        Optional<Role> roleOpt = roleDao.findRolesByTenantIdAndTitle(role.getTenantId().getId(), role.getTitle());
        roleOpt.ifPresent(
                r -> {
                    if (!r.getId().equals(role.getId())) {
                        throw new DataValidationException("Role with such title already exists!");
                    }
                }
        );
        return roleOpt.orElse(null);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Role role) {
        if (StringUtils.isEmpty(role.getTitle())) {
            throw new DataValidationException("Role title should be specified!");
        }
        if (role.getTenantId() == null) {
            throw new DataValidationException("Role should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(role.getTenantId())) {
                throw new DataValidationException("Role is referencing to non-existent tenant!");
            }
        }
    }
}
