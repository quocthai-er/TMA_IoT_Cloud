package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class RoleId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public RoleId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static RoleId fromString(String roleId) {
        return new RoleId(UUID.fromString(roleId));
    }

    @Override
    @ApiModelProperty(position = 2, required = true, value = "string", example = "ROLE", allowableValues = "ROLE")
    public EntityType getEntityType() {
        return EntityType.ROLE;
    }
}

