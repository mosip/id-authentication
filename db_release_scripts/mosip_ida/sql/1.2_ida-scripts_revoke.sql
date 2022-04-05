-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.1.5
-- Purpose    		: Revoking Database Alter deployement done for release in ID Authentication DB.       
-- Create By   		: Ram Bhatt
-- Created Date		: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------
-- Apr-2021		Ram Bhatt	    create tables to store partner details
-- Sep-2021		Loganathan Sekar	    create anonymous_profile table
-------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

ALTER TABLE IF EXISTS ida.uin_auth_lock DROP COLUMN unlock_expiry_datetime;

-- -------------------------------------------------------------------------------------------------


DROP TABLE IF EXISTS ida.partner_data;
DROP TABLE IF EXISTS ida.policy_data;
DROP TABLE IF EXISTS ida.api_key_data;
DROP TABLE IF EXISTS ida.partner_mapping;

DROP TABLE IF EXISTS ida.misp_license_data;

-----------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS ida.anonymous_profile;