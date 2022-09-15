package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class RoleId extends UUIDBased {

    private static final long serialVersionUID = -4530267273428275504L;
    @JsonCreator
    public RoleId(@JsonProperty("id") UUID id) {super(id);}
    public static RoleId fromString(String roleId) {
        return new RoleId(UUID.fromString(roleId));
    }

}

