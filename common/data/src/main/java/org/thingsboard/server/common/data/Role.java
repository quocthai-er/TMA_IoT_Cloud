package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.validation.Length;

public class Role extends BaseData<RoleId> {

    @Length(fieldName = "title")
    private String title;

    @Length(fieldName = "operations")
    private JsonNode operations;

    public Role() {
        super();
    }

    public Role(RoleId id) {
        super(id);
    }

    public Role(Role role) {
        super(role);
        this.title = role.getTitle();
        this.operations = role.getOperations();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Role Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer." )
    @Override
    public RoleId getId() { return super.getId(); }

    @ApiModelProperty(position = 2, value = "Timestamp of the role creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "Title of the role", example = "MANAGER")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(position = 4, value = "List operations of role")
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
