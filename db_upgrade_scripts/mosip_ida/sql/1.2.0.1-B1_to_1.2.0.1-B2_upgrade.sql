\c mosip_ida

ALTER TABLE ida.misp_license_data ADD policy_id character varying(50);

ALTER TABLE ida.partner_mapping ALTER COLUMN api_key_id TYPE varchar(100);

-- DROP TABLE IF EXISTS ida.oidc_client_data CASCADE;
CREATE TABLE ida.oidc_client_data (
	oidc_client_id character varying(100) NOT NULL,
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

GRANT SELECT, INSERT, REFERENCES, UPDATE, DELETE
   ON ida.oidc_client_data
   TO idauser;

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

GRANT SELECT, INSERT, REFERENCES, UPDATE, DELETE
   ON ida.kyc_token_store
   TO idauser;