CREATE TABLE ida.key_policy_def_h(
    app_id character varying(36) NOT NULL,
    eff_dtimes timestamp NOT NULL,
    key_validity_duration smallint,
    is_active boolean NOT NULL,
    pre_expire_days smallint,
    access_allowed character varying(1024),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_keypdefh_id PRIMARY KEY (app_id,eff_dtimes)
);
COMMENT ON TABLE ida.key_policy_def_h IS 'Key Policy Definition History : This to track changes to master record whenever there is an INSERT/UPDATE/DELETE ( soft delete ), Effective DateTimestamp is used for identifying latest or point in time information. Refer kernel.key_policy_def table description for details.';
COMMENT ON COLUMN ida.key_policy_def_h.app_id IS 'Application ID: Application id for which the key policy is defined';
COMMENT ON COLUMN ida.key_policy_def_h.eff_dtimes IS 'Effective Date Timestamp : This to track master record whenever there is an INSERT/UPDATE/DELETE ( soft delete ).  The current record is effective from this date-time. ';
COMMENT ON COLUMN ida.key_policy_def_h.key_validity_duration IS 'Key Validity Duration: Duration for which key is valid';
COMMENT ON COLUMN ida.key_policy_def_h.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active';
COMMENT ON COLUMN ida.key_policy_def_h.cr_by IS 'Created By : ID or name of the user who create / insert record';
COMMENT ON COLUMN ida.key_policy_def_h.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN ida.key_policy_def_h.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN ida.key_policy_def_h.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN ida.key_policy_def_h.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
COMMENT ON COLUMN ida.key_policy_def_h.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
