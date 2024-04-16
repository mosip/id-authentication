\c mosip_ida

ALTER TABLE ida.credential_event_store ALTER COLUMN credential_transaction_id type character varying(64);

DROP INDEX IF EXISTS ida.ind_otphsh;

CREATE INDEX ind_refid ON ida.otp_transaction (ref_id,status_code);
