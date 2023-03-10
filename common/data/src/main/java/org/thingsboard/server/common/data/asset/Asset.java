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
package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class Asset extends SearchTextBasedWithAdditionalInfo<AssetId> implements HasName, HasTenantId, HasCustomerId, ExportableEntity<AssetId> {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @NoXss
    @Length(fieldName = "label")
    private String label;

    @Getter @Setter
    private AssetId externalId;

    @Length(fieldName = "avatar", max = 1000000)
    @ApiModelProperty(position = 15, value = "Either URL or Base64 data of the avatar")
    private String avatar;

    public Asset() {
        super();
    }

    public Asset(AssetId id) {
        super(id);
    }

    public Asset(Asset asset) {
        super(asset);
        this.tenantId = asset.getTenantId();
        this.customerId = asset.getCustomerId();
        this.name = asset.getName();
        this.type = asset.getType();
        this.label = asset.getLabel();
        this.externalId = asset.getExternalId();
        this.avatar = asset.getAvatar();
    }

    public void update(Asset asset) {
        this.tenantId = asset.getTenantId();
        this.customerId = asset.getCustomerId();
        this.name = asset.getName();
        this.type = asset.getType();
        this.label = asset.getLabel();
        Optional.ofNullable(asset.getAdditionalInfo()).ifPresent(this::setAdditionalInfo);
        this.externalId = asset.getExternalId();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the asset Id. " +
            "Specify this field to update the asset. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new asset.")
    @Override
    public AssetId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the asset creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, value = "JSON object with Customer Id. Use 'assignAssetToCustomer' to change the Customer Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @ApiModelProperty(position = 5, required = true, value = "Unique Asset Name in scope of Tenant", example = "Empire State Building")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 6, required = true, value = "Asset type", example = "Building")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(position = 7, required = true, value = "Label that may be used in widgets", example = "NY Building")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getSearchText() {
        return getName();
    }

    @ApiModelProperty(position = 8, value = "Additional parameters of the asset", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    public String getAvatar() {
        return avatar;
    }

    private String getDefaultAvatar() {
        String defaultAvatar = "";
        try {
            //Get default avatar file from dao/src/main/resources/img/avatar.png
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("img/avatar.png");
            byte[] avatarBytes = IOUtils.toByteArray(is);
            //Convert avatar data to base 64 string..
            defaultAvatar = "data:image/png;base64,".concat(Base64Utils.encodeToString(avatarBytes));
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Default avatar is not found or its size is too large");
        }
        return defaultAvatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Asset [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", label=");
        builder.append(label);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
