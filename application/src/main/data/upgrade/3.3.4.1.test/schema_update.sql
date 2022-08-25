--Update Thingsboard customized database

ALTER TABLE customer ADD COLUMN IF NOT EXISTS avatar varchar(1000000);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS phone character varying(255) COLLATE pg_catalog."default";

DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM pg_constraint WHERE conname = 'tb_user_phone_key') THEN
            ALTER TABLE tb_user ADD CONSTRAINT tb_user_phone_key UNIQUE (phone);
        END IF;
    END;
$$;



