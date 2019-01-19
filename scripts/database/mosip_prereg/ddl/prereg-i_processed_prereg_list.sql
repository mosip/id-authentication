-- object: prereg.i_processed_prereg_list | type: TABLE --
-- DROP TABLE IF EXISTS prereg.i_processed_prereg_list CASCADE;
CREATE TABLE prereg.i_processed_prereg_list(
	prereg_id character varying(36) NOT NULL,
	received_dtimes timestamp NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT ipprlst_pk PRIMARY KEY (prereg_id,received_dtimes)

);
-- ddl-end --
COMMENT ON TABLE prereg.i_processed_prereg_list IS 'Table to store all the pre-registration list received from registration processor within pre-registration module';
-- ddl-end --
COMMENT ON COLUMN prereg.i_processed_prereg_list.prereg_id IS 'Pre-registration id that was consumed by registration processor to generate UIN';
-- ddl-end --
COMMENT ON COLUMN prereg.i_processed_prereg_list.received_dtimes IS 'Datetime when the pre-registration id was recevied';
-- ddl-end --
ALTER TABLE prereg.i_processed_prereg_list OWNER TO appadmin;
-- ddl-end --

