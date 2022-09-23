--
-- Copyright © 2016-2022 The Thingsboard Authors
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--Update Thingsboard customized database

ALTER TABLE customer ADD COLUMN IF NOT EXISTS avatar varchar(1000000);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS phone varchar(255) COLLATE pg_catalog."default";

--start new
CREATE TABLE IF NOT EXISTS tb_role (
    id uuid NOT NULL CONSTRAINT role_pkey PRIMARY KEY,
    title varchar(255) NOT NULL,
    permissions varchar,
    tenant_id uuid,
    created_time bigint,
    search_text varchar(255)
);

ALTER TABLE customer
    ADD COLUMN IF NOT EXISTS role_id uuid;

ALTER TABLE customer
    ADD CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES tb_role (id);
--end new

DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'tb_user_phone_key') THEN
            ALTER TABLE tb_user ADD CONSTRAINT tb_user_phone_key UNIQUE (phone);
        END IF;
    END;
$$;



