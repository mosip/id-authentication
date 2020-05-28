-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.0.9
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 28-May-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

\ir ../ddl/ida-identity_cache.sql
\ir ../ddl/ida-data_encrypt_keystore.sql

----------------------------------------------------------------------------------------------------