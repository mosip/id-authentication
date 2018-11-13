-- object: uin.uin_detail_h | type: TABLE --
-- DROP TABLE IF EXISTS uin.uin_detail_h CASCADE;
CREATE TABLE uin.uin_detail_h(
	uin_ref_id character varying(28) NOT NULL,
	eff_dtimes timestamp NOT NULL,
	uin_data bytea NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT uind_h_pk PRIMARY KEY (uin_ref_id,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE uin.uin_detail_h IS 'History of the changes to the individual details. individual data is stored in json structure.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.uin_ref_id IS 'UIN reference id of an individual from uin table. ';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.eff_dtimes IS 'When an individual''s inforamtion is changed, the history of changes is maintained in this table. Record active effective datetime.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.uin_data IS 'Data about the individual in json structure. This field contains data like demographic, biometric, iris, etc. The data is stored in multiple languages inside the json file.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail_h.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE uin.uin_detail_h OWNER TO appadmin;
-- ddl-end --


