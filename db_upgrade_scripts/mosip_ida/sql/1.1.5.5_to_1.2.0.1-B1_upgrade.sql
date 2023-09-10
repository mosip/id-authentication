\c mosip_ida

REASSIGN OWNED BY sysadmin TO postgres;

REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA ida FROM idauser;

REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA ida FROM sysadmin;

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE ON ALL TABLES IN SCHEMA ida TO idauser;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ida TO postgres;

DROP TABLE IF EXISTS ida.api_key_data CASCADE;
DROP TABLE IF EXISTS ida.partner_data CASCADE;
DROP TABLE IF EXISTS ida.partner_mapping CASCADE;
DROP TABLE IF EXISTS ida.policy_data CASCADE;
DROP TABLE IF EXISTS ida.misp_license_data CASCADE;
ALTER TABLE ida.uin_auth_lock ADD COLUMN unlock_expiry_datetime timestamp;
-------------------------------------------------------------------------------------------------------

-- object: ida.api_key_data | type: TABLE --
CREATE TABLE ida.api_key_data (
	api_key_id character varying(36) NOT NULL,
	api_key_commence_on timestamp NOT NULL,
	api_key_expires_on timestamp,
	api_key_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT api_key_data_pk PRIMARY KEY (api_key_id)

);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.api_key_data
   TO idauser;
   
--index section starts----
CREATE INDEX ind_akd_apkeyid ON ida.api_key_data (api_key_id);
--index section ends------



-- object: ida.partner_data | type: TABLE --
CREATE TABLE ida.partner_data (
	partner_id character varying(36) NOT NULL,
	partner_name character varying(128) NOT NULL,
	certificate_data bytea ,
	partner_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_data_pk PRIMARY KEY (partner_id)

);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.partner_data
   TO idauser;
--index section starts----
CREATE INDEX ind_pd_pid ON ida.partner_data (partner_id);
--index section ends------




-- object: ida.partner_mapping | type: TABLE --
CREATE TABLE ida.partner_mapping (
	partner_id character varying(36) NOT NULL,
	policy_id character varying(36) NOT NULL,
	api_key_id character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_mapping_pk PRIMARY KEY (partner_id,policy_id,api_key_id)

);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.partner_mapping
   TO idauser;
--index section starts----
CREATE INDEX ind_pm_pid ON ida.partner_mapping (partner_id);
--index section ends------



-- object: ida.policy_data | type: TABLE --
CREATE TABLE ida.policy_data (
	policy_id character varying(36) NOT NULL,
	policy_data bytea NOT NULL,
	policy_name character varying(128) NOT NULL,
	policy_status character varying(36) NOT NULL,
	policy_description character varying(256),
	policy_commence_on timestamp NOT NULL,
	policy_expires_on timestamp,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT policy_data_pk PRIMARY KEY (policy_id)

);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.policy_data
   TO idauser;

--index section starts----
CREATE INDEX ind_pl_pid ON ida.policy_data (policy_id);
--index section ends------


-- object: ida.misp_license_data | type: TABLE --
CREATE TABLE ida.misp_license_data (
	misp_id character varying(36) NOT NULL,
	license_key character varying(128) NOT NULL,
	misp_commence_on timestamp NOT NULL,
	misp_expires_on timestamp,
	misp_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT misp_license_data_pk PRIMARY KEY (misp_id)

);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.misp_license_data
   TO idauser;
--index section starts----
CREATE INDEX ind_mld_lk ON ida.misp_license_data (license_key);
--index section ends------



-- object: ida.anonymous_profile | type: TABLE --
-- DROP TABLE IF EXISTS ida.anonymous_profile CASCADE;
CREATE TABLE ida.anonymous_profile(
	id character varying(36) NOT NULL,
	profile character varying NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_profile PRIMARY KEY (id)
);

GRANT SELECT, INSERT, TRUNCATE, REFERENCES, UPDATE, DELETE
   ON ida.anonymous_profile
   TO idauser;
-- ddl-end --
COMMENT ON TABLE ida.anonymous_profile IS 'anonymous_profile: Anonymous profiling information for reporting purpose.';
COMMENT ON COLUMN ida.anonymous_profile.id IS 'Reference ID: System generated id for references in the system.';
COMMENT ON COLUMN ida.anonymous_profile.profile IS 'Profile : Contains complete anonymous profile data generated by ID-Repository and stored in plain json text format.';
COMMENT ON COLUMN ida.anonymous_profile.cr_by IS 'Created By : ID or name of the user who create / insert record';
COMMENT ON COLUMN ida.anonymous_profile.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN ida.anonymous_profile.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN ida.anonymous_profile.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN ida.anonymous_profile.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
COMMENT ON COLUMN ida.anonymous_profile.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';

--------------------------------------------------------------------------------------------------------------


ALTER TABLE ida.identity_cache ADD COLUMN identity_expiry timestamp;


ALTER TABLE ida.ca_cert_store ADD CONSTRAINT cert_thumbprint_unique UNIQUE (cert_thumbprint,partner_domain);


ALTER TABLE ida.key_alias ADD COLUMN uni_ident character varying(50);
ALTER TABLE ida.key_alias ADD CONSTRAINT uni_ident_const UNIQUE (uni_ident);

ALTER TABLE ida.key_policy_def ADD COLUMN pre_expire_days smallint;
ALTER TABLE ida.key_policy_def ADD COLUMN access_allowed character varying(1024);

---------------------------------------------------------------------------------------------------------------

ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET DEFAULT FALSE;

update ida.key_policy_def set pre_expire_days=90, access_allowed='NA' where app_id='ROOT';
update ida.key_policy_def set pre_expire_days=30, access_allowed='NA' where app_id='BASE';
update ida.key_policy_def set pre_expire_days=60, access_allowed='NA' where app_id='IDA';
