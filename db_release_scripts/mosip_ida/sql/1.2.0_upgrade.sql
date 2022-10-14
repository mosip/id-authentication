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

CREATE INDEX ind_akd_apkeyid ON ida.api_key_data (api_key_id);
CREATE INDEX ind_pm_pid ON ida.partner_mapping (partner_id);
CREATE INDEX ind_pd_pid ON ida.partner_data (partner_id);
CREATE INDEX ind_mld_lk ON ida.misp_license_data (license_key);
CREATE INDEX ind_pd_pyid ON ida.policy_data (policy_id);
CREATE INDEX ind_reqtrnid_dtimes_tknid ON ida.auth_transaction (request_trn_id, request_dtimes, token_id, cr_dtimes, auth_type_code);
CREATE INDEX ind_ces_id ON ida.credential_event_store (cr_dtimes);
CREATE INDEX ind_hc_idhsh_etp ON ida.hotlist_cache (id_hash, expiry_timestamp);
CREATE INDEX ind_id ON ida.identity_cache (id);
CREATE INDEX ind_otphsh ON ida.otp_transaction (otp_hash,status_code);
CREATE INDEX ind_ual_id ON ida.uin_auth_lock (token_id);
CREATE INDEX ind_uhs_id ON ida.uin_hash_salt (id);


-----------------------------------------------------------------------------------------------------------
ALTER TABLE ida.key_alias ADD COLUMN cert_thumbprint character varying(100);
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
