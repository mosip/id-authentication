-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.2
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: Sep-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

DROP TABLE IF EXISTS ida.uin_encrypt_salt;
DROP TABLE IF EXISTS ida.token_seed;
DROP TABLE IF EXISTS ida.token_seq;

ALTER TABLE ida.key_store ALTER COLUMN private_key TYPE character varying(2500);
ALTER TABLE ida.key_store ALTER COLUMN certificate_data TYPE character varying(2500);

DROP TABLE IF EXISTS ida.uin_auth_lock;
DROP TABLE IF EXISTS ida.otp_transaction;

\ir ../ddl/ida-uin_auth_lock.sql
\ir ../ddl/ida-otp_transaction.sql

ALTER TABLE ida.auth_transaction DROP COLUMN IF EXISTS uin;
ALTER TABLE ida.auth_transaction DROP COLUMN IF EXISTS uin_hash;
ALTER TABLE ida.auth_transaction ADD COLUMN IF NOT EXISTS token_id character varying(128) NOT NULL DEFAULT 'default_token';

ALTER TABLE ida.identity_cache ADD COLUMN IF NOT EXISTS token_id character varying(128) NOT NULL DEFAULT 'default_token';

-------------- Level 1 data load scripts ------------------------

----- TRUNCATE ida.key_policy_def TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def cascade ;

\COPY ida.key_policy_def (app_id,key_validity_duration,is_active,cr_by,cr_dtimes) FROM './dml/ida-key_policy_def.csv' delimiter ',' HEADER  csv;


----- TRUNCATE ida.key_policy_def_h TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def_h cascade ;

\COPY ida.key_policy_def_h (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,eff_dtimes) FROM './dml/ida-key_policy_def_h.csv' delimiter ',' HEADER  csv;


----------------------------------------------------------------------------------------------------