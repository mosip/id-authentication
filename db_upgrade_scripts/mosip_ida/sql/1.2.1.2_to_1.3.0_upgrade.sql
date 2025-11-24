-- ------------------------------------------------------------------------------------------
-- Upgrade script for Migrating Spring batch version to 5.0 as part of Java 21 Migration.
-- ------------------------------------------------------------------------------------------
\c mosip_ida
ALTER TABLE BATCH_STEP_EXECUTION ADD CREATE_TIME TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00';
ALTER TABLE BATCH_STEP_EXECUTION ALTER COLUMN START_TIME DROP NOT NULL;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS DROP COLUMN DATE_VAL;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS DROP COLUMN LONG_VAL;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS DROP COLUMN DOUBLE_VAL;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN TYPE_CD TYPE VARCHAR(100);
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME TYPE_CD TO PARAMETER_TYPE;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN KEY_NAME TYPE VARCHAR(100);
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME KEY_NAME TO PARAMETER_NAME;
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS ALTER COLUMN STRING_VAL TYPE VARCHAR(2500);
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS RENAME STRING_VAL TO PARAMETER_VALUE;
ALTER TABLE BATCH_JOB_EXECUTION DROP COLUMN JOB_CONFIGURATION_LOCATION;

CREATE INDEX IF NOT EXISTS idx_job_name ON BATCH_JOB_INSTANCE(JOB_NAME);
CREATE INDEX IF NOT EXISTS idx_job_key ON BATCH_JOB_INSTANCE(JOB_KEY);

-- Below script required to upgraded from 1.3.0-beta.1 to 1.3.0
--------ca_cert_store-upgrade-db script------------
ALTER TABLE IF EXISTS ida.ca_cert_store ADD COLUMN ca_cert_type character varying(25);


-- Optimize autovacuum for anonymous_profile to handle moderate updates
ALTER TABLE anonymous_profile SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 500,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 500
);

ALTER TABLE api_key_data SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX idx_autntxn_refid_dtimes 
ON ida.auth_transaction (ref_id, request_dtimes);

CREATE INDEX CONCURRENTLY idx_auth_txn_entityid_request_dtimes
ON ida.auth_transaction (requested_entity_id, request_dtimes DESC);

CREATE INDEX idx_autn_txn_refid_time_desc
ON ida.auth_transaction (ref_id, request_dtimes DESC);

-- Create index to support paginated filtered query
CREATE INDEX idx_autntxn_reqtrnid_authtype_crdtimes_desc
ON ida.auth_transaction (request_trn_id, auth_type_code, cr_dtimes DESC);
CREATE INDEX idx_autntxn_token_crdtimes_desc
ON ida.auth_transaction (token_id, cr_dtimes DESC);
CREATE INDEX idx_autntxn_token_reqdtimes
ON ida.auth_transaction (token_id, request_dtimes);

ALTER TABLE ida.auth_transaction SET (
    autovacuum_vacuum_scale_factor = 0.002,
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_scale_factor = 0.002,
    autovacuum_analyze_threshold = 5000
);

-- Optimize autovacuum for batch_job_execution to clean dead tuples
ALTER TABLE batch_job_execution SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

-- Optimize autovacuum for batch_job_execution_context to clean dead tuples
ALTER TABLE batch_job_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

-- Optimize autovacuum for batch_job_execution_params to clean dead tuples
ALTER TABLE batch_job_execution_params SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

-- Optimize autovacuum for batch_job_instance to clean dead tuples
ALTER TABLE batch_job_instance SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

-- Optimize autovacuum for batch_step_execution to clean dead tuples
ALTER TABLE batch_step_execution SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 2000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 2000
);

-- Optimize autovacuum for batch_step_execution_context to clean dead tuples
ALTER TABLE batch_step_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 5000
);

-- Optimize autovacuum for ca_cert_store to clean dead tuples
ALTER TABLE ca_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Optimize autovacuum for cred_subject_id_store to clean dead tuples
ALTER TABLE cred_subject_id_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX idx_cred_evt_pending
ON credential_event_store (retry_count, cr_dtimes)
WHERE status_code IN ('NEW', 'FAILED');

-- Optimize autovacuum for credential_event_store to clean dead tuples
ALTER TABLE credential_event_store SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

-- Optimize autovacuum for data_encrypt_keystore to clean dead tuples
ALTER TABLE data_encrypt_keystore SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hotlist_idhash_idtype
ON ida.hotlist_cache (id_hash, id_type);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hotlist_active
ON ida.hotlist_cache (id_hash, id_type, status)
WHERE status = 'Blocked';

-- Optimize autovacuum for hotlist_cache to clean dead tuples
ALTER TABLE hotlist_cache SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 10,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 10
);

-- Optimize autovacuum for ident_binding_cert_store to clean dead tuples
ALTER TABLE ident_binding_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 10,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 10
);

ALTER TABLE identity_cache SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 500,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 500
);

-- Optimize autovacuum for key_alias to clean dead tuples
ALTER TABLE key_alias SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 5,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 5
);

-- Optimize autovacuum for key_policy_def to clean dead tuples
ALTER TABLE key_policy_def SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

-- Optimize autovacuum for key_policy_def_h to clean dead tuples
ALTER TABLE key_policy_def_h SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

-- Optimize autovacuum for key_store to clean dead tuples
ALTER TABLE key_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

-- Optimize autovacuum for kyc_token_store to clean dead tuples
ALTER TABLE kyc_token_store SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 100,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 100
);

-- Optimize autovacuum for misp_license_data to clean dead tuples
ALTER TABLE misp_license_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Optimize autovacuum for oidc_client_data to clean dead tuples
ALTER TABLE oidc_client_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX idx_otp_txn_ref_status_gen 
ON ida.otp_transaction (ref_id, status_code, generated_dtimes DESC);

-- Optimize autovacuum for otp_transaction to clean dead tuples
ALTER TABLE otp_transaction SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 100,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 100
);

-- Optimize autovacuum for partner_data to clean dead tuples
ALTER TABLE partner_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Optimize autovacuum for partner_mapping to clean dead tuples
ALTER TABLE partner_mapping SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE policy_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE uin_auth_lock SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

ALTER TABLE uin_hash_salt SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

-- Below script required to upgrade from 1.3.0-B1 to 1.3.0-B2
-- ca_cert_type column is added to the ca_cert_store table --
ALTER TABLE IF EXISTS ida.ca_cert_store ADD COLUMN ca_cert_type character varying(25);
