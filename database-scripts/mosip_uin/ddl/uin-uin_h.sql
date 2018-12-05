-- object: uin.uin_h | type: TABLE --
-- DROP TABLE IF EXISTS uin.uin_h CASCADE;
CREATE TABLE uin.uin_h(
	uin_ref_id character varying(28) NOT NULL,
	eff_dtimes timestamp NOT NULL,
	uin character(28) NOT NULL,
	status_code character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT uinh_pk PRIMARY KEY (uin_ref_id,eff_dtimes),
	CONSTRAINT uinh_uk UNIQUE (uin,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE uin.uin_h IS 'History table to store UINs of individuals and its status change. This table will not contain any data related to an individuals other than status.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.uin_ref_id IS 'UIN reference identification, a unique identity field that will be used in all reference tables.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.eff_dtimes IS 'Effective datetime ';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.uin IS 'Unique identification number of an individual';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.status_code IS 'Status code of the UIN. This field refers to the master list of status defined in master schema.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_h.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE uin.uin_h OWNER TO appadmin;
-- ddl-end --

