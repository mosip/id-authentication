-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.data_encrypt_keystore
-- Purpose    	: Data Encrypt Keystore: Table is used to store the encryption key aliases which is used encrypt the data stored in identity cache table store.
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 19-May-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- 
-- ------------------------------------------------------------------------------------------

-- object: ida.data_encrypt_keystore | type: TABLE --
-- DROP TABLE IF EXISTS ida.data_encrypt_keystore CASCADE;
CREATE TABLE ida.data_encrypt_keystore(
	id bigint NOT NULL,
	key character varying(64) NOT NULL,
	key_status character varying(16),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	CONSTRAINT pk_dekstr_id PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE ida.data_encrypt_keystore IS 'Data Encrypt Keystore: Table is used to store the encryption key aliases which is used encrypt the data stored in identity cache table store.';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.id IS 'Id: Id of an encryption key alias';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.key IS 'Key: Encryption key, Key which is used to encrypt the data used in identity cache tables store';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.key_status IS 'Key Status: Status of the key for ex. Key is expired, active or any other state which is defined in system';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.data_encrypt_keystore.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --