package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.validation.Length;

public class Role extends BaseData<RoleId> {

    @Length(fieldName = "title")
    @ApiModelProperty(position = 3, value = "Title of the role", example = "MANAGER")
    private String title;

    @Length(fieldName = "operations")
    @ApiModelProperty(position = 4, value = "List operations of role")
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

    @ApiModelProperty(position = 2, value = "Timestamp of the customer creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
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
