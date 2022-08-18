-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.1.3
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: Nov-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

------------------------------ ID Auth Alter Scripts Deploymnet ------------------------------------

ALTER TABLE ida.auth_transaction ADD COLUMN IF NOT EXISTS request_signature character varying;
ALTER TABLE ida.auth_transaction ADD COLUMN IF NOT EXISTS response_signature character varying;

-----------------------------------------------------------------------------------------------------