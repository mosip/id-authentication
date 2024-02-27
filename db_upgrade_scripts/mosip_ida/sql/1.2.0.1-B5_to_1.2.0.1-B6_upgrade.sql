-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.2.0.1-B6
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Mahammed Taheer
-- Created Date		: Feb-2024
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
\c mosip_ida

ALTER TABLE ida.credential_event_store ALTER COLUMN credential_transaction_id type character varying(64);

DROP INDEX IF EXISTS ida.ind_otphsh;

CREATE INDEX ind_refid ON ida.otp_transaction (ref_id,status_code);

