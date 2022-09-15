package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.SearchTextEntity;

public class RoleEntity extends BaseSqlEntity<Role> implements SearchTextEntity<Role> {

    private String title;
    private String searchText;
    private JsonNode operations;

    @Override
    public String getSearchTextSource() {
        return this.title;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public Role toData() {
        Role role = new Role(new RoleId(this.getUuid()));
        role.setCreatedTime(createdTime);
        role.setTitle(title);
        role.setOperations(operations);
        return role;
    }
}
