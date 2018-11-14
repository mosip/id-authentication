-- object: regprc.individual_demographic_dedup | type: TABLE --
-- DROP TABLE IF EXISTS regprc.individual_demographic_dedup CASCADE;
CREATE TABLE regprc.individual_demographic_dedup(
	ref_id_type character varying(36) NOT NULL,
	ref_id character varying(28) NOT NULL,
	first_name character varying(128),
	middle_name character varying(128),
	last_name character varying(128),
	full_name character varying(256),
	dob date,
	gender character varying(36),
	address_line_1 character varying(256),
	address_line_2 character varying(256),
	address_line_3 character varying(256),
	address_line_4 character varying(256),
	address_line_5 character varying(256),
	address_line_6 character varying(256),
	zip_code character varying(36),
	lang_code character varying(3) NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT idemog_pk PRIMARY KEY (ref_id_type,ref_id,lang_code)

);
-- ddl-end --
COMMENT ON TABLE regprc.individual_demographic_dedup IS 'individual demographic table stores applicant demographic details for deduplication';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.ref_id_type IS 'type of the reference id. It can have values like prereg_id, reg_id or uin_ref_id.';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.ref_id IS 'Reference id can be registration id of new registrations or uin_ref_id of individuals whose uin is already generated';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.first_name IS 'first name of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.middle_name IS 'Middle Name of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.last_name IS 'Last Name of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.full_name IS 'Full Name of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.dob IS 'DOB of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.gender IS 'Gender of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_1 IS 'Address Line 1 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_2 IS 'Address Line 2 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_3 IS 'Address Line 3 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_4 IS 'Address Line 4 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_5 IS 'Address Line 5 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.address_line_6 IS 'Address Line 6 of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.zip_code IS 'Zip / Pin / Postal Code of an individual';
-- ddl-end --
ALTER TABLE regprc.individual_demographic_dedup OWNER TO appadmin;
-- ddl-end --

