-- ------------------------------------------------------------------------------------------
-- Rollback script for migrating Spring Batch back from 6.0 (Spring Boot 4 migration).
-- ------------------------------------------------------------------------------------------
ALTER SEQUENCE BATCH_JOB_INSTANCE_SEQ RENAME TO BATCH_JOB_SEQ;
