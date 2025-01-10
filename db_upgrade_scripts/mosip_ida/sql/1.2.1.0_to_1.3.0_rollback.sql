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
