-- object: uin.uin | type: TABLE --
-- DROP TABLE IF EXISTS uin.uin CASCADE;
CREATE TABLE uin.uin(
	uin_ref_id character varying(28) NOT NULL,
	uin character varying(28) NOT NULL,
	status_code character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT uin_pk PRIMARY KEY (uin_ref_id),
	CONSTRAINT uin_uk UNIQUE (uin)

);
-- ddl-end --
COMMENT ON TABLE uin.uin IS 'Table to store UINs of individuals. This table will not contain any data related to an individual, individuals details are stored in a separate table mapped to uin_ref_id.';
-- ddl-end --
COMMENT ON COLUMN uin.uin.uin_ref_id IS 'UIN reference identification, a unique identity field that will be used in all reference tables.';
-- ddl-end --
COMMENT ON COLUMN uin.uin.uin IS 'Unique identification number of an individual';
-- ddl-end --
COMMENT ON COLUMN uin.uin.status_code IS 'Status code of the UIN. This field refers to the master list of status defined in master schema.';
-- ddl-end --
COMMENT ON COLUMN uin.uin.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN uin.uin.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN uin.uin.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN uin.uin.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE uin.uin OWNER TO appadmin;
-- ddl-end --
