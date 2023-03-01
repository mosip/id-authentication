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

CREATE TABLE ida.ident_binding_cert_store (
	cert_id character varying(36) NOT NULL,
	id_vid_hash character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	certificate_data character varying NOT NULL,
	public_key_hash character varying(1024) NOT NULL,
	cert_thumbprint character varying(100) NOT NULL,
	partner_name character varying(128) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp
);
-- ddl-end --

INSERT INTO ida.key_policy_def (app_id, key_validity_duration, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, pre_expire_days, access_allowed) 
VALUES('IDA_KYC_EXCHANGE', 1095, true, 'mosipadmin', now(), NULL, NULL, false, NULL, 60, 'NA');

INSERT INTO ida.key_policy_def (app_id, key_validity_duration, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, pre_expire_days, access_allowed) 
VALUES('IDA_KEY_BINDING', 1095, true, 'mosipadmin', now(), NULL, NULL, false, NULL, 60, 'NA');
