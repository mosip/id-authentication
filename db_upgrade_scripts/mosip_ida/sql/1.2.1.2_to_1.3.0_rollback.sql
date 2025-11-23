-- ------------------------------------------------------------------------------------------
-- Rollback script for Migrating Spring batch version back from 5.0 as part of Java 21 Migration.
-- ------------------------------------------------------------------------------------------
-- Below script required to upgrade from 1.3.0-B2 to 1.3.0
\c mosip_ida
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

-- Below script required to rollback from 1.3.0-beta.1 to 1.3.0.
----------ca_cert_store-rollback- db script-------------
ALTER TABLE IF EXISTS ida.ca_cert_store DROP COLUMN IF EXISTS ca_cert_type;

-- Rollback autovacuum settings to defaults
ALTER TABLE anonymous_profile RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE api_key_data RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE ida.auth_transaction RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_job_execution RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_job_execution_context RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_job_execution_params RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_job_instance RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_step_execution RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE batch_step_execution_context RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE ca_cert_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE cred_subject_id_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE credential_event_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE data_encrypt_keystore RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE hotlist_cache RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE ident_binding_cert_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE identity_cache RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE key_alias RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE key_policy_def RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE key_policy_def_h RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE key_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE kyc_token_store RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE misp_license_data RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE oidc_client_data RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE otp_transaction RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE partner_data RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE partner_mapping RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE policy_data RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE uin_auth_lock RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

ALTER TABLE uin_hash_salt RESET (
    autovacuum_vacuum_scale_factor,
    autovacuum_vacuum_threshold,
    autovacuum_analyze_scale_factor,
    autovacuum_analyze_threshold
);

-- Drop indexes created in upgrade
DROP INDEX IF EXISTS idx_autntxn_refid_dtimes;
DROP INDEX CONCURRENTLY IF EXISTS idx_auth_txn_entityid_request_dtimes;
DROP INDEX IF EXISTS idx_autn_txn_refid_time_desc;
DROP INDEX IF EXISTS idx_autntxn_reqtrnid_authtype_crdtimes_desc;
DROP INDEX IF EXISTS idx_autntxn_token_crdtimes_desc;
DROP INDEX IF EXISTS idx_autntxn_token_reqdtimes;
DROP INDEX IF EXISTS idx_cred_evt_pending;
DROP INDEX CONCURRENTLY IF EXISTS idx_hotlist_idhash_idtype;
DROP INDEX CONCURRENTLY IF EXISTS idx_hotlist_active;
DROP INDEX IF EXISTS idx_otp_txn_ref_status_gen;


-- Below script required to rollback from 1.3.0-B2 to 1.3.0-B1
-- ca_cert_type column is removed/deleted from ca_cert_store table --
ALTER TABLE IF EXISTS ida.ca_cert_store DROP COLUMN IF EXISTS ca_cert_type;
