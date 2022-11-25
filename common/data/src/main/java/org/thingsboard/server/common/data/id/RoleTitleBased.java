/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.id;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.io.Serializable;

@ApiModel
public abstract class RoleTitleBased implements Serializable {

    //private static final long serialVersionUID = 1L;

    private final String roleTitle;

    public RoleTitleBased(String roleTitle) {
        super();
        this.roleTitle = roleTitle;
    }

    @ApiModelProperty(position = 1, required = true, value = "string", example = "TENANT_ADMIN")
    public String getTitle() {
        return roleTitle;
    }

 /*   @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        return result;
    }
*/
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoleTitleBased other = (RoleTitleBased) obj;
        if (roleTitle == null) {
            if (other.roleTitle != null)
                return false;
        } else if (!roleTitle.equals(other.roleTitle))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return roleTitle.toString();
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "ROLE", allowableValues = "ROLE")
    public abstract EntityType getEntityType();
}
