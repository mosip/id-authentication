\c mosip_ida sysadmin

DROP TABLE IF EXISTS ida.api_key_data CASCADE;
DROP TABLE IF EXISTS ida.partner_data CASCADE;
DROP TABLE IF EXISTS ida.partner_mapping CASCADE;
DROP TABLE IF EXISTS ida.policy_data CASCADE;
DROP TABLE IF EXISTS ida.misp_license_data CASCADE;
ALTER TABLE ida.uin_auth_lock ADD COLUMN unlock_expiry_datetime timestamp;
-------------------------------------------------------------------------------------------------------

\ir ../ddl/ida-api_key_data.sql
\ir ../ddl/ida-partner_data.sql
\ir ../ddl/ida-partner_mapping.sql
\ir ../ddl/ida-policy_data.sql


\ir ../ddl/ida-misp_license_data.sql
----------------------------------------------------------------------------------------------------------

\ir ../ddl/ida-anonymous_profile.sql

ALTER TABLE ida.identity_cache ADD COLUMN identity_expiry timestamp;
--------------------------------------------------------------------------------------------------------------

ALTER TABLE ida.ca_cert_store ADD CONSTRAINT cert_thumbprint_unique UNIQUE (cert_thumbprint,partner_domain);

--------------------------------------------------------------------------------------------------------------
ALTER TABLE ida.key_alias ADD COLUMN uni_ident character varying(50);
ALTER TABLE ida.key_alias ADD CONSTRAINT uni_ident_const UNIQUE (uni_ident);

ALTER TABLE ida.key_policy_def ADD COLUMN pre_expire_days smallint;
ALTER TABLE ida.key_policy_def ADD COLUMN access_allowed character varying(1024);

ALTER TABLE ida.key_policy_def_h ADD COLUMN pre_expire_days smallint;
ALTER TABLE ida.key_policy_def_h ADD COLUMN access_allowed character varying(1024);
---------------------------------------------------------------------------------------------------------------

ALTER TABLE ida.uin_auth_lock ALTER COLUMN is_deleted SET DEFAULT FALSE;

update ida.key_policy_def set pre_expire_days=90, access_allowed='NA' where app_id='ROOT';
update ida.key_policy_def set pre_expire_days=30, access_allowed='NA' where app_id='BASE';
update ida.key_policy_def set pre_expire_days=60, access_allowed='NA' where app_id='IDA';

