-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.2
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Ram Bhatt
-- Created Date		: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

----------------------------------------------------------------------------------------------------
\c mosip_ida sysadmin

ALTER TABLE ida.uin_auth_lock ADD COLUMN unlock_timestamp timestamp;

----------------------------------------------------------------------------------------------------------


