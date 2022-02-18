CREATE TABLE ida.key_alias(
    id character varying(36) NOT NULL,
    app_id character varying(36) NOT NULL,
    ref_id character varying(128),
    key_gen_dtimes timestamp,
    key_expire_dtimes timestamp,
    status_code character varying(36),
    lang_code character varying(3),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    cert_thumbprint character varying(100),
    uni_ident character varying(50),
    CONSTRAINT pk_keymals_id PRIMARY KEY (id),
    CONSTRAINT uni_ident_const UNIQUE (uni_ident)
);
COMMENT ON TABLE ida.key_alias IS 'Key Alias: To maintain a system generated key as alias for the encryption key that will be stored in key-store devices like HSM.';
COMMENT ON COLUMN ida.key_alias.id IS 'ID: Key alias id is a unique identifier (UUID) used as an alias of the encryption key stored in keystore like HSM (hardware security module).';
COMMENT ON COLUMN ida.key_alias.app_id IS 'Application ID: Application id for which the encryption key is generated';
COMMENT ON COLUMN ida.key_alias.ref_id IS 'Reference ID: Reference ID is a reference inforamtion received from key requester which can be machine id, TSP id, etc.';
COMMENT ON COLUMN ida.key_alias.key_gen_dtimes IS 'Key Generated Date Time: Date and time when the key was generated.';
COMMENT ON COLUMN ida.key_alias.key_expire_dtimes IS 'Key Expiry Date Time: Date and time when the key will be expired. This will be derived based on the configuration / policy defined in Key policy definition.';
COMMENT ON COLUMN ida.key_alias.status_code IS 'Status Code: Status of the key, whether it is active or expired.';
COMMENT ON COLUMN ida.key_alias.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language.';
COMMENT ON COLUMN ida.key_alias.cr_by IS 'Created By : ID or name of the user who create / insert record';
COMMENT ON COLUMN ida.key_alias.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN ida.key_alias.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN ida.key_alias.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN ida.key_alias.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
COMMENT ON COLUMN ida.key_alias.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
