-- object: uin.uin_detail | type: TABLE --
-- DROP TABLE IF EXISTS uin.uin_detail CASCADE;
CREATE TABLE uin.uin_detail(
	uin_data json NOT NULL,
	is_active uin._active NOT NULL,
	cr_by uin._by NOT NULL,
	cr_dtimes uin._dtimes NOT NULL,
	upd_by uin._by,
	upd_dtimes uin._dtimes,
	is_deleted uin._active,
	del_dtimes uin._dtimes,
	uin_ref_id uin._uinvid
);
-- ddl-end --
COMMENT ON TABLE uin.uin_detail IS 'Table to store UIN object';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.uin_data IS 'This filed contains the json packet of the individual''s information';
-- ddl-end --
COMMENT ON COLUMN uin.uin_detail.is_active IS 'Record active status';
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

