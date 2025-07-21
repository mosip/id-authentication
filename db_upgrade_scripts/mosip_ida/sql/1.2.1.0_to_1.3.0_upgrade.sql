-- ------------------------------------------------------------------------------------------
-- Upgrade script for Migrating Spring batch version to 5.0 as part of Java 21 Migration.
-- ------------------------------------------------------------------------------------------
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

CREATE UNIQUE INDEX idx_partner_mapping_apikey ON partner_mapping;

--------------------------------------------------------------------------------------------------
-- ca_cert_store Upgrade Script
--------------------------------------------------------------------------------------------------
ALTER TABLE IF EXISTS ida.ca_cert_store ADD COLUMN ca_cert_type character varying(25);

-- 1. auth_transaction: for countByRefIdAndRequestDTtimesAfter
DROP INDEX IF EXISTS idx_autntxn_refid_dtimes;
CREATE INDEX idx_autntxn_refid_dtimes 
  ON ida.auth_transaction (ref_id, request_dtimes);

-- 2. credential_event_store: for fetching pending credential events
DROP INDEX IF EXISTS idx_cred_evt_pending;
CREATE INDEX idx_cred_evt_pending 
  ON credential_event_store (retry_count, cr_dtimes)
  WHERE status_code IN ('NEW', 'FAILED');

-- 3. hotlist_cache: for findByIdHashAndIdType
DROP INDEX IF EXISTS idx_hotlistcache_hash_type;
CREATE INDEX idx_hotlistcache_hash_type 
  ON ida.hotlist_cache (id_hash, id_type);

-- 4. otp_transaction: for findFirstByRefIdAndStatusCodeInOrderByGeneratedDtimesDesc
DROP INDEX IF EXISTS idx_otp_txn_ref_status_gen;
CREATE INDEX idx_otp_txn_ref_status_gen 
  ON ida.otp_transaction (ref_id, status_code, generated_dtimes DESC);
  
  -- drop first (so script is idempotent if re‑run)
  DROP INDEX IF EXISTS idx_autntxn_entityid_dtimes;
  
  -- create composite index
  CREATE INDEX idx_autntxn_entityid_dtimes
    ON ida.auth_transaction (requested_entity_id, request_dtimes);
    
    
-- Drop if exists for safety
DROP INDEX IF EXISTS idx_autntxn_reqtrnid_authtype_crdtimes_desc;

-- Create index to support paginated filtered query
CREATE INDEX idx_autntxn_reqtrnid_authtype_crdtimes_desc
ON ida.auth_transaction (request_trn_id, auth_type_code, cr_dtimes DESC);

-- Drop first for idempotency
DROP INDEX IF EXISTS idx_autntxn_token_crdtimes_desc;

-- Create index to speed up findByToken with pagination
CREATE INDEX idx_autntxn_token_crdtimes_desc
ON ida.auth_transaction (token_id, cr_dtimes DESC);

-- Safe drop first
DROP INDEX IF EXISTS idx_autntxn_token_reqdtimes;

-- Create composite index for countRequestDTime()
CREATE INDEX idx_autntxn_token_reqdtimes
ON ida.auth_transaction (token_id, request_dtimes);
