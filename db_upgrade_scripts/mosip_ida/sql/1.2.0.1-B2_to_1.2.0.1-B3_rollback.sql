\c mosip_ida

ALTER TABLE ida.kyc_token_store DROP COLUMN request_trn_id;

DROP TABLE IF EXISTS ida.ident_binding_cert_store CASCADE;

DELETE FROM ida.key_policy_def WHERE app_id='IDA_KYC_EXCHANGE';

DELETE FROM ida.key_policy_def WHERE app_id='IDA_KEY_BINDING'

