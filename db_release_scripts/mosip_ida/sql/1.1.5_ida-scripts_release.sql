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
-- Feb-2021		Ram Bhatt	    Added hotlist table in ida
-- Feb-2021		Ram Bhatt	    Changed size of auth_type_code from 32 to 128
-- Mar-2021		Ram Bhatt	    Reverting is_deleted not null changes
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

--------- --------------ALTER TABLE SCRIPT DEPLOYMENT ------------------------------------------------

--ALTER TABLE ida.auth_transaction ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.identity_cache ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.key_policy_def_h ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.key_policy_def ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.key_store ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.key_alias ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.otp_transaction ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.credential_event_store ALTER COLUMN is_deleted SET NOT NULL;
--ALTER TABLE ida.ca_cert_store ALTER COLUMN is_deleted SET NOT NULL;

--ALTER TABLE ida.auth_transaction ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.identity_cache ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.key_policy_def_h ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.key_policy_def ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.key_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.key_alias ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.otp_transaction ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.credential_event_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
--ALTER TABLE ida.ca_cert_store ALTER COLUMN is_deleted SET DEFAULT FALSE;
-------------------------------------------------------------------------------------------------------
--------------------------------------HOTLISTING TABLE DEPLOYMENT-----------------------------------------

\ir ../ddl/ida-hotlist_cache.sql

----------------------------------------------------------------------------------------------------------
--------------------------------------AUTH TYPE CODE SIZE CHANGE-----------------------------------------


ALTER TABLE ida.auth_transaction ALTER COLUMN auth_type_code TYPE character varying(128);

----------------------------------------------------------------------------------------------------------

CREATE INDEX ind_reqtrnid_dtimes_tknid ON ida.auth_transaction (request_trn_id, request_dtimes, token_id,cr_dtimes, auth_type_code);
CREATE INDEX ind_ces_id ON ida.credential_event_store (cr_dtimes);
CREATE INDEX ind_hc_idhsh_etp ON ida.hotlist_cache (id_hash, expiry_timestamp);
CREATE INDEX ind_id ON ida.identity_cache (id);
CREATE INDEX ind_otphsh ON ida.otp_transaction (otp_hash,status_code);
CREATE INDEX ind_ual_id ON ida.uin_auth_lock (token_id);
CREATE INDEX ind_uhs_id ON ida.uin_hash_salt (id);

-----------------------------------------------------------------------------------------------------------


