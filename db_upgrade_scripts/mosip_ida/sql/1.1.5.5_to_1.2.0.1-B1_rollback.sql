\c mosip_ida

REASSIGN OWNED BY postgres TO sysadmin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ida TO sysadmin;

ALTER TABLE ida.uin_auth_lock DROP COLUMN unlock_expiry_datetime;

-- -------------------------------------------------------------------------------------------------


DROP TABLE IF EXISTS ida.partner_data;
DROP TABLE IF EXISTS ida.policy_data;
DROP TABLE IF EXISTS ida.api_key_data;
DROP TABLE IF EXISTS ida.partner_mapping;

DROP TABLE IF EXISTS ida.misp_license_data;

-----------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS ida.anonymous_profile;


-------------------------------------------------------------------------------

ALTER TABLE ida.identity_cache DROP COLUMN identity_expiry;
ALTER TABLE ida.ca_cert_store DROP CONSTRAINT cert_thumbprint_unique;

ALTER TABLE ida.key_alias DROP COLUMN uni_ident;
ALTER TABLE ida.key_alias DROP CONSTRAINT uni_ident_const;

ALTER TABLE ida.key_policy_def DROP COLUMN pre_expire_days;
ALTER TABLE ida.key_policy_def DROP COLUMN access_allowed;

ALTER TABLE ida.key_policy_def DROP COLUMN pre_expire_days;
ALTER TABLE ida.key_policy_def DROP COLUMN access_allowed;


----------------------------------------------------------

