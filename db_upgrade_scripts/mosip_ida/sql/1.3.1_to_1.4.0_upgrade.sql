-- ------------------------------------------------------------------------------------------
-- Upgrade script for migrating Spring Batch to 6.0 as part of the Spring Boot 4 migration.
-- The batch_* tables are unchanged; only the job instance sequence was renamed.
-- ------------------------------------------------------------------------------------------
ALTER SEQUENCE BATCH_JOB_SEQ RENAME TO BATCH_JOB_INSTANCE_SEQ;
