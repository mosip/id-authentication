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

ALTER TABLE ida.misp_license_data ADD policy_id character varying(50);

-- DROP TABLE IF EXISTS ida.oidc_client_data CASCADE;
CREATE TABLE ida.oidc_client_data (
	oidc_client_id character varying(36) NOT NULL,
	oidc_client_name character varying(128) NOT NULL,
	oidc_client_status character varying(36) NOT NULL,
	user_claims character varying(1024) NOT NULL,
	auth_context_refs character varying(1024) NOT NULL,
	client_auth_methods character varying(1024) NOT NULL,
	partner_id character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT oidc_client_data_pk PRIMARY KEY (oidc_client_id)

);

-- DROP TABLE IF EXISTS ida.kyc_token_store CASCADE;
CREATE TABLE ida.kyc_token_store(
    id character varying(36) NOT NULL,
    id_vid_hash character varying(128) NOT NULL,
    kyc_token character varying(128),
    psu_token character varying(128),
    oidc_client_id character varying(128),
    token_issued_dtimes timestamp,
    auth_req_dtimes timestamp,
    kyc_token_status character varying(36),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_key_id PRIMARY KEY (id),
    CONSTRAINT kyc_token_const UNIQUE (kyc_token)
);