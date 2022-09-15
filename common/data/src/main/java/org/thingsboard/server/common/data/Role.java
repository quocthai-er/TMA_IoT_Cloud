package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.RoleId;

public class Role extends BaseData<RoleId> {
    private String title;
    private transient JsonNode operations;
    public Role() {
        super();
    }

    public Role(RoleId id) {
        super(id);
    }

    public Role(Role role) {
        super(role);
    }

    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JsonNode getOperations() {
        return this.operations;
    }
    public void setOperations(JsonNode operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [title=");
        builder.append(getTitle());
        builder.append(", operations=");
        builder.append(operations);
        builder.append("]");
        return builder.toString();
    }

}
