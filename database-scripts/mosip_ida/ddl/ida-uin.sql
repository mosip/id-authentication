-- object: ida.uin | type: TABLE --
-- DROP TABLE IF EXISTS ida.uin CASCADE;
CREATE TABLE ida.uin(
	uin_ref_id 	character varying(28) NOT NULL,
	uin 		character(28) NOT NULL,
	
	cr_by 		character varying(32) NOT NULL,
	cr_dtimes 	timestamp NOT NULL,
	upd_by 		character varying(32),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp,
	
	CONSTRAINT pk_uin_ref_id PRIMARY KEY (uin_ref_id),
	CONSTRAINT uk_uin UNIQUE (uin)
);
-- ddl-end --
COMMENT ON TABLE ida.uin IS 'Table to store UINs of individuals. This table will not contain any data related to an individual, individuals details are stored in a separate table mapped to uin_ref_id.';
-- ddl-end --
COMMENT ON COLUMN ida.uin.uin_ref_id IS 'UIN reference identification, a unique identity field that will be used in all reference tables.';
-- ddl-end --
COMMENT ON COLUMN ida.uin.uin IS 'Unique identification number of an individual';
-- ddl-end --
COMMENT ON COLUMN ida.uin.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN ida.uin.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN ida.uin.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN ida.uin.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN ida.uin.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN ida.uin.del_dtimes IS 'Record deleted datetime';
-- ddl-end --

