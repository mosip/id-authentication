-- object: idrepo.uin_h | type: TABLE --
-- DROP TABLE IF EXISTS idrepo.uin_h CASCADE;
CREATE TABLE idrepo.uin_h(
	uin_ref_id character varying(36) NOT NULL,
	eff_dtimes timestamp NOT NULL,
	uin character varying(28) NOT NULL,
	uin_data bytea NOT NULL,
	uin_data_hash character varying(64) NOT NULL,
	reg_id character varying(39) NOT NULL,
	status_code character varying(32) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_uinh PRIMARY KEY (uin_ref_id,eff_dtimes),
	CONSTRAINT uk_uinh UNIQUE (uin,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE idrepo.uin_h IS 'Table to store history of the individual data and the assigned UIN. This table will also contain the  registration id that was used to update individual''s information.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.uin_ref_id IS 'UIN reference identification, a unique identity field that will be used in all reference tables.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.uin IS 'Unique identification number of an individual';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.status_code IS 'Status code of the UIN. This field refers to the master list of status defined in master schema.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.lang_code IS 'Language code of the status code field. Refernces status master table from master DB.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_h.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE idrepo.uin_h OWNER TO sysadmin;
-- ddl-end --

