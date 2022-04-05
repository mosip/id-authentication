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
-- Jul-2021		Ram Bhatt	    creation of failed message store table
-- Jul-2021		Ram Bhatt	    Adding a new nullable column identity_expiry in IDA table identity_cache
-- Sep-2021		Loganathan Sekar	    Adding Anonymous Profile Table
-- Sep-2021		Ram Bhatt	    Adding indices to multiple tables
-- Oct-2021		Loganathan Sekar	    Removed failed_message_store table
----------------------------------------------------------------------------------------------------
\c mosip_ida sysadmin

ALTER TABLE ida.key_policy_def_h ADD COLUMN pre_expire_days smallint;
ALTER TABLE ida.key_policy_def_h ADD COLUMN access_allowed character varying(1024);
---------------------------------------------------------------------------------------------------------------

ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET DEFAULT FALSE;








