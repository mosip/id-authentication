-- ca_cert_type column is removed/deleted from ca_cert_store table --
ALTER TABLE IF EXISTS master.ca_cert_store DROP COLUMN IF EXISTS ca_cert_type;