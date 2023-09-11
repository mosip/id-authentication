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
\c mosip_ida

ALTER TABLE ida.kyc_token_store ADD request_trn_id character varying(64);

CREATE TABLE ida.ident_binding_cert_store (
	cert_id character varying(36) NOT NULL,
	id_vid_hash character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	certificate_data character varying NOT NULL,
	public_key_hash character varying(1024) NOT NULL,
	cert_thumbprint character varying(100) NOT NULL,
	partner_name character varying(128) NOT NULL,
	auth_factor character varying(100) NOT NULL,
	cert_expire timestamp NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT uni_public_key_hash_const UNIQUE (public_key_hash)
);

GRANT SELECT, INSERT, REFERENCES, UPDATE, DELETE
   ON ida.ident_binding_cert_store
   TO idauser;
-- ddl-end --

INSERT INTO ida.key_policy_def (app_id, key_validity_duration, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, pre_expire_days, access_allowed) 
VALUES('IDA_KYC_EXCHANGE', 1095, true, 'mosipadmin', now(), NULL, NULL, false, NULL, 60, 'NA');

INSERT INTO ida.key_policy_def (app_id, key_validity_duration, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, pre_expire_days, access_allowed) 
VALUES('IDA_KEY_BINDING', 1095, true, 'mosipadmin', now(), NULL, NULL, false, NULL, 60, 'NA');
