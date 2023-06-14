\c mosip_ida

ALTER TABLE ida.misp_license_data DROP COLUMN policy_id;

ALTER TABLE ida.partner_mapping ALTER COLUMN api_key_id TYPE varchar(36);

DROP TABLE IF EXISTS ida.oidc_client_data CASCADE;

DROP TABLE IF EXISTS ida.kyc_token_store CASCADE;