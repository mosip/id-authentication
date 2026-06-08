-- Below script required to upgrade from 1.3.0-B2 to 1.3.0
\c mosip_ida
CREATE INDEX IF NOT EXISTS cred_event_store_status_cr_dtimes ON ida.credential_event_store USING btree (status_code desc, retry_count, cr_dtimes) WHERE status_code in ('NEW','FAILED');