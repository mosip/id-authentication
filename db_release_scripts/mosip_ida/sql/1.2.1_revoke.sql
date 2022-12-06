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

ALTER TABLE ida.misp_license_data DROP COLUMN policy_id;

DROP TABLE IF EXISTS ida.oidc_client_data CASCADE;

DROP TABLE IF EXISTS ida.kyc_token_store CASCADE;