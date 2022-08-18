-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.1.4
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: Dec-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

\ir ../ddl/ida-ca_cert_store.sql

----------------------------------------------------------------------------------------------------