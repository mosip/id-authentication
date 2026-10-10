-- Below script required to upgrade from 1.3.0-B2 to 1.3.0
\c :mosipdbname
DROP INDEX IF EXISTS cred_event_store_status_cr_dtimes;