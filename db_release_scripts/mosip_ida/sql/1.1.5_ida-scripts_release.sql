-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.1.5
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: Jan-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------
-- Jan-2021		Ram Bhatt	    Set is_deleted flag to not null and default false
----------------------------------------------------------------------------------------------------
\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

\ir ../ddl/ida-credential_event_store.sql


\ir ../ddl/ida-batch_job_execution.sql
\ir ../ddl/ida-batch_job_execution_context.sql
\ir ../ddl/ida-batch_job_execution_params.sql
\ir ../ddl/ida-batch_job_instance.sql
\ir ../ddl/ida-batch_step_execution.sql
\ir ../ddl/ida-batch_step_execution_context.sql

\ir ../ddl/ida-fk.sql

----------------------------------------------------------------------------------------------------

--------- ------------ALTER TABLE SCRIPT DEPLOYMENT ------------------------------------------------

ALTER TABLE ida.auth_transaction ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.identity_cache ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.key_policy_def_h ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.key_policy_def ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.key_store ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.key_alias ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.otp_transaction ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.credential_event_store ALTER COLUMN is_deleted SET NOT NULL;
ALTER TABLE ida.ca_cert_store ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE ida.auth_transaction ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.identity_cache ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.key_policy_def_h ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.key_policy_def ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.key_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.key_alias ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.otp_transaction ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.credential_event_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE ida.ca_cert_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
-------------------------------------------------------------------------------------------------------


