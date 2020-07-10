-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.0.10
-- Purpose    		: Revoking Database Alter deployement done for release in ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 10-Jul-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

ALTER TABLE ida.key_alias DROP CONSTRAINT IF EXISTS uk_keyals_appref; 
-- -------------------------------------------------------------------------------------------------