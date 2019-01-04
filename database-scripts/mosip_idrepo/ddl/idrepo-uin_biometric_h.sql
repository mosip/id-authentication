-- object: idrepo.uin_biometric_h | type: TABLE --
-- DROP TABLE IF EXISTS idrepo.uin_biometric_h CASCADE;
CREATE TABLE idrepo.uin_biometric_h(
	uin_ref_id character varying(36) NOT NULL,
	eff_dtimes timestamp NOT NULL,
	bio_file_id character varying(128) NOT NULL,
	biometric_file_name character varying(128) NOT NULL,
	biometric_file_hash character varying(64) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_uinbh PRIMARY KEY (uin_ref_id,eff_dtimes),
	CONSTRAINT uk_uinbh UNIQUE (bio_file_id,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE idrepo.uin_biometric_h IS 'Table to store history of biometric reference and hash value of the biometric file. This table will keep the history of changes to the biometric information. ';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.uin_ref_id IS 'reference id of an uin, refers to uin_ref_id of idrep.uin table';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.biometric_file_name IS 'Name of the biometric cbeff file';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.biometric_file_hash IS 'Hash value of the biometric dbeff file';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.lang_code IS 'Language code of the biometic file name.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_biometric_h.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE idrepo.uin_biometric_h OWNER TO appadmin;
-- ddl-end --

