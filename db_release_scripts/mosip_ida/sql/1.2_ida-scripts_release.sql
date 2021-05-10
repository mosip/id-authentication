-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.2
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Ram Bhatt
-- Created Date		: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------
-- Apr-2021		Ram Bhatt	    create tables to store partner details
----------------------------------------------------------------------------------------------------
\c mosip_ida sysadmin

DROP TABLE IF EXISTS ida.api_key_data CASCADE;
DROP TABLE IF EXISTS ida.partner_data CASCADE;
DROP TABLE IF EXISTS ida.partner_mapping CASCADE;
DROP TABLE IF EXISTS ida.policy_data CASCADE;
DROP TABLE IF EXISTS ida.misp_license_data CASCADE;
--ALTER TABLE ida.uin_auth_lock ADD COLUMN unlock_expiry_datetime timestamp;
-------------------------------------------------------------------------------------------------------

\ir ../ddl/ida-api_key_data.sql
\ir ../ddl/ida-partner_data.sql
\ir ../ddl/ida-partner_mapping.sql
\ir ../ddl/ida-policy_data.sql


\ir ../ddl/ida-misp_license_data.sql
----------------------------------------------------------------------------------------------------------


