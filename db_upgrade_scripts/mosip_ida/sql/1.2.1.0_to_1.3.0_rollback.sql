-- ------------------------------------------------------------------------------------------
-- Rollback script for Migrating Spring batch version back from 5.0 as part of Java 21 Migration.
-- ------------------------------------------------------------------------------------------

TRUNCATE TABLE batch_job_execution CASCADE;
TRUNCATE TABLE batch_job_execution_context CASCADE;
TRUNCATE TABLE batch_job_execution_params CASCADE;
TRUNCATE TABLE batch_job_instance CASCADE;
TRUNCATE TABLE batch_step_execution CASCADE;
TRUNCATE TABLE batch_step_execution_context CASCADE;

GRANT usage, SELECT ON ALL SEQUENCES
   IN SCHEMA ida
   TO idauser;

-- Revert ALTER on BATCH_STEP_EXECUTION
ALTER TABLE BATCH_STEP_EXECUTION DROP COLUMN CREATE_TIME;  -- Remove the column added
ALTER TABLE BATCH_STEP_EXECUTION ALTER COLUMN START_TIME SET NOT NULL;  -- Revert to NOT NULL

-- Revert changes on BATCH_JOB_EXECUTION_PARAMS
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ADD COLUMN DATE_VAL TIMESTAMP;  -- Add back the DATE_VAL column
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ADD COLUMN LONG_VAL BIGINT;     -- Add back the LONG_VAL column
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ADD COLUMN DOUBLE_VAL DOUBLE PRECISION;  -- Add back the DOUBLE_VAL column

ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME PARAMETER_TYPE TO TYPE_CD;            -- Revert the column name
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN TYPE_CD TYPE VARCHAR(100);

ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN PARAMETER_NAME TYPE VARCHAR(100);  -- Revert back the type change
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME PARAMETER_NAME TO KEY_NAME;              -- Revert the column name

ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN PARAMETER_VALUE TYPE VARCHAR(250); -- Revert the type change
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME PARAMETER_VALUE TO STRING_VAL;           -- Revert the column name

-- Revert DROP on BATCH_JOB_EXECUTION
ALTER TABLE BATCH_JOB_EXECUTION ADD COLUMN JOB_CONFIGURATION_LOCATION VARCHAR(2500);  -- Add back the column

-- Drop the indices if they were created
DROP INDEX IF EXISTS idx_job_name;
DROP INDEX IF EXISTS idx_job_key;
DROP INDEX IF EXISTS idx_partner_mapping_apikey;

---------------------------------------------------------------------------------------------------
-- ca_cert_store db roll back script
---------------------------------------------------------------------------------------------------
ALTER TABLE IF EXISTS ida.ca_cert_store DROP COLUMN IF EXISTS ca_cert_type;

-- Rollback: Drop indexes for auth performance tuning

-- 1. auth_transaction
DROP INDEX IF EXISTS idx_autntxn_refid_dtimes;

-- 2. credential_event_store
DROP INDEX IF EXISTS idx_cred_evt_pending;

-- 3. hotlist_cache
DROP INDEX IF EXISTS idx_hotlist_idhash_idtype;

DROP INDEX IF EXISTS idx_hotlist_active;

-- 4. otp_transaction
DROP INDEX IF EXISTS idx_otp_txn_ref_status_gen;


DROP INDEX IF EXISTS idx_auth_txn_entityid_request_dtimes;

-- Rollback: Drop the index
DROP INDEX IF EXISTS idx_autntxn_reqtrnid_authtype_crdtimes_desc;

-- Rollback: drop the token+cr_dtimes index
DROP INDEX IF EXISTS idx_autntxn_token_crdtimes_desc;

-- Rollback: drop the index
DROP INDEX IF EXISTS idx_autntxn_token_reqdtimes;

ALTER TABLE anonymous_profile SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for api_key_data to default values
ALTER TABLE api_key_data SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE auth_transaction SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_job_execution to default values
ALTER TABLE batch_job_execution SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_job_execution_context to default values
ALTER TABLE batch_job_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_job_execution_params to default values
ALTER TABLE batch_job_execution_params SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_job_instance to default values
ALTER TABLE batch_job_instance SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_step_execution to default values
ALTER TABLE batch_step_execution SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for batch_step_execution_context to default values
ALTER TABLE batch_step_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for ca_cert_store to default values
ALTER TABLE ca_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for cred_subject_id_store to default values
ALTER TABLE cred_subject_id_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for credential_event_store to default values
ALTER TABLE credential_event_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for data_encrypt_keystore to default values
ALTER TABLE data_encrypt_keystore SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for hotlist_cache to default values
ALTER TABLE hotlist_cache SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE ident_binding_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE identity_cache SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for key_alias to default values
ALTER TABLE key_alias SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for key_policy_def to default values
ALTER TABLE key_policy_def SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for key_policy_def_h to default values
ALTER TABLE key_policy_def_h SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for key_store to default values
ALTER TABLE key_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for kyc_token_store to default values
ALTER TABLE kyc_token_store SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for misp_license_data to default values
ALTER TABLE misp_license_data SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for otp_transaction to default values
ALTER TABLE otp_transaction SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for partner_mapping to default values
ALTER TABLE partner_mapping SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Rollback autovacuum settings for partner_mapping to default values
ALTER TABLE policy_data SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE uin_hash_salt SET (
    autovacuum_vacuum_scale_factor = 0.2,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

DROP INDEX IF EXISTS idx_autn_txn_refid_time_desc;