-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.key_store
-- Purpose    	: Key Store: In MOSIP, data related to an individual in stored in encrypted form. This table is to manage all the keys(private and public keys) used.
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 20-Apr-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------- -----------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- object: ida.key_store | type: TABLE --
-- DROP TABLE IF EXISTS ida.key_store CASCADE;
CREATE TABLE ida.key_store(
	id character varying(36) NOT NULL,
	master_key character varying(36) NOT NULL,
	private_key character varying(2500) NOT NULL,
	certificate_data character varying(2500) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_keystr_id PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE ida.key_store IS 'Key Store: In MOSIP, data related to an individual in stored in encrypted form. This table is to manage all the keys(private and public keys) used. ';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.id IS 'ID: ID is a unique identifier (UUID) used for managing encryption keys';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.master_key IS 'Master Key: Master key is used to encrypt the other keys (Public / Private)';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.private_key IS 'Private Key: Private key';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.certificate_data IS 'Certificate Data:';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN ida.key_store.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
