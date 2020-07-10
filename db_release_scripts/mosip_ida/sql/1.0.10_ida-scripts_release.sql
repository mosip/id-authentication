-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.0.10
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 10-Jul-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------
ALTER TABLE ida.key_alias ADD CONSTRAINT uk_keyals_appref UNIQUE (app_id,ref_id);

----------------------------------------------------------------------------------------------------