\c mosip_ida sysadmin

ALTER TABLE ida.uin_auth_lock DROP COLUMN unlock_expiry_datetime;

-- -------------------------------------------------------------------------------------------------


DROP TABLE IF EXISTS ida.partner_data;
DROP TABLE IF EXISTS ida.policy_data;
DROP TABLE IF EXISTS ida.api_key_data;
DROP TABLE IF EXISTS ida.partner_mapping;

DROP TABLE IF EXISTS ida.misp_license_data;

-----------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS ida.anonymous_profile;
