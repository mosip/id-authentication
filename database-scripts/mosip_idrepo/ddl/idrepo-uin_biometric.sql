-- object: idrepo.uin_biometric | type: TABLE --
-- DROP TABLE IF EXISTS idrepo.uin_biometric CASCADE;
CREATE TABLE idrepo.uin_biometric(
	uin_ref_id character varying(28) NOT NULL,
	bio_file_id character varying(128) NOT NULL,
	biometric_file_name character varying(128),
	biometric_file_hash character varying(64) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT uk_uinb UNIQUE (bio_file_id),
	CONSTRAINT pk_uinb PRIMARY KEY (uin_ref_id)

);
-- ddl-end --
COMMENT ON TABLE idrepo.uin_biometric IS 'Table to store biometric reference and hash value of the biometric file. ';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.biometric_file_name IS 'Name of the biometric cbeff file';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.biometric_file_hash IS 'Hash value of the biometric dbeff file';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.lang_code IS 'Language code of the biometic file name.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE idrepo.uin_biometric OWNER TO appadmin;
-- ddl-end --


