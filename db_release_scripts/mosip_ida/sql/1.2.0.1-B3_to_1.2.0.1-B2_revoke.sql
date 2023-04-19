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

ALTER TABLE ida.kyc_token_store DROP COLUMN request_trn_id;

DROP TABLE IF EXISTS ida.ident_binding_cert_store CASCADE;

DELETE FROM ida.key_policy_def WHERE app_id='IDA_KYC_EXCHANGE';

DELETE FROM ida.key_policy_def WHERE app_id='IDA_KEY_BINDING'

