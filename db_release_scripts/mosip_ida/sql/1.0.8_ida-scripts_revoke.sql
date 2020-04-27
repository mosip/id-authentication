-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.0.8
-- Purpose    		: Revoking Database Alter deployement done for release in ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 22-Apr-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

DROP TABLE IF EXISTS ida.key_alias;
DROP TABLE IF EXISTS ida.key_store;
DROP TABLE IF EXISTS ida.key_policy_def;
DROP TABLE IF EXISTS ida.key_policy_def_h;

-- -------------------------------------------------------------------------------------------------