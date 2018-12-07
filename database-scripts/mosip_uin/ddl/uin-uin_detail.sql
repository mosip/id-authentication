-- object: uin.uin_detail | type: TABLE --
-- DROP TABLE IF EXISTS uin.uin_detail CASCADE;
CREATE TABLE uin.uin_detail(
	uin_ref_id character varying(28) NOT NULL,
	uin_data bytea NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT uind_pk PRIMARY KEY (uin_ref_id)

);
-- ddl-end --
COMMENT ON TABLE uin.uin_detail IS 'Table to store details of the UINs. This table will contain the data of an individual in json structure.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.uin_data IS 'Data about the individual in json structure. This field contains data like demographic, biometric, iris, etc. The data is stored in multiple languages inside the json file.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE uin.uin_detail OWNER TO appadmin;
-- ddl-end --

-- object: uind_uk | type: CONSTRAINT --
-- ALTER TABLE uin.uin_detail DROP CONSTRAINT IF EXISTS uind_uk CASCADE;
ALTER TABLE uin.uin_detail ADD CONSTRAINT uind_uk UNIQUE (uin_ref_id);
-- ddl-end --


