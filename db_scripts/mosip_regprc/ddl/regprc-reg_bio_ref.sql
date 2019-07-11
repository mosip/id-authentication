-- NOTE: the code below contains the SQL for the selected object
-- as well for its dependencies and children (if applicable).
-- 
-- This feature is only a convinience in order to permit you to test
-- the whole object's SQL definition at once.
-- 
-- When exporting or generating the SQL for the whole database model
-- all objects will be placed at their original positions.


-- object: regprc.reg_bio_ref | type: TABLE --
-- DROP TABLE IF EXISTS regprc.reg_bio_ref CASCADE;
CREATE TABLE regprc.reg_bio_ref(
	reg_id character varying(39) NOT NULL,
	bio_ref_id character varying(36) NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_regbref_id PRIMARY KEY (reg_id)

);
-- ddl-end --
COMMENT ON TABLE regprc.reg_bio_ref IS 'Registration Biometric Reference: Mapping table to store the bio reference id for an registration id';
-- ddl-end --
ALTER TABLE regprc.reg_bio_ref OWNER TO sysadmin;
-- ddl-end --

