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
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.DeviceInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfoEntity extends AbstractDeviceEntity<DeviceInfo> {

    public static final Map<String,String> deviceInfoColumnMap = new HashMap<>();
    static {
        deviceInfoColumnMap.put("customerTitle", "c.title");
        deviceInfoColumnMap.put("deviceProfileName", "p.name");
    }

    private String customerTitle;
    private boolean customerIsPublic;
    private String deviceProfileName;

    public DeviceInfoEntity() {
        super();
    }

    public DeviceInfoEntity(DeviceEntity deviceEntity,
                            String customerTitle,
                            Object customerAdditionalInfo,
                            String deviceProfileName) {
        super(deviceEntity);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }
        this.deviceProfileName = deviceProfileName;
    }

    public DeviceInfoEntity (DeviceEntitys deviceEntitys,
                            String customerTitle,
                            Object customerAdditionalInfo,
                            String deviceProfileName) {
        super(deviceEntitys);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }
        this.deviceProfileName = deviceProfileName;
    }

  /* public DeviceInfoEntity(UUID tenantId, UUID customerId, String type, String name, String label, String searchText,
                            JsonNode additionalInfo, UUID deviceProfileId, UUID firmwareId, UUID softwareId,
                            JsonNode deviceData, UUID externalId,
                            String customerTitle,
                            Object customerAdditionalInfo,
                            String deviceProfileName) {
        super(tenantId, customerId, type, name, label,searchText,additionalInfo, deviceProfileId, firmwareId, softwareId,deviceData, externalId);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }
        this.deviceProfileName = deviceProfileName;
    }*/

    @Override
    public DeviceInfo toData() {
        return new DeviceInfo(super.toDevice(), customerTitle, customerIsPublic, deviceProfileName);
    }
}
