
ALTER TABLE ida.credential_event_store ALTER COLUMN credential_transaction_id type character varying(36);

CREATE INDEX ind_otphsh ON ida.otp_transaction (otp_hash,status_code);

DROP INDEX IF EXISTS ida.ind_refid;

