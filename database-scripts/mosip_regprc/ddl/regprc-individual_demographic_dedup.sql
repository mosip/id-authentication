-- create table section ---------------------------------------------------
-- schema 		: regprc					- Registration Processor (enrolment server or ID issuance server)
-- table 		: individual_demographic_dedup	- Registration Processor / Enrolment Packet and Transactions.
-- table alias  : idemogd

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists

create schema if not exists regprc
;
-- object: regprc.individual_demographic_dedup | type: TABLE --
-- DROP TABLE IF EXISTS regprc.individual_demographic_dedup CASCADE;

CREATE TABLE regprc.individual_demographic_dedup(
	reg_id 			character varying(39) not null,	   -- regprc.registration.id
	uin_ref_id		character varying(28),
	name 			character varying(128),            -- Name of an individual, This is combination of fname, mname and lname or full name
	phonetic_name 	character varying(128),
	dob 	date,
	gender 	character varying(64),

	lang_code 	character varying(3) not null,
	is_active 	boolean not null,
	cr_by 		character varying(32) not null,
	cr_dtimes 	timestamp not null,
	upd_by 		character varying(32),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp

);

-- keys section ----------------------------------------------------------------
 alter table regprc.individual_demographic_dedup add constraint pk_idemogd_id primary key (reg_id, lang_code)
 ; 

-- ddl-end --
COMMENT ON TABLE regprc.individual_demographic_dedup IS 'Individual demographic table stores applicant demographic details for deduplication';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.reg_id IS 'Registration id of applicant';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.uin_ref_id IS 'UIN reference id of Individuals';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.name IS 'Name of an individual, This is combination of fname, mname and lname or full name';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.phonetic_name IS 'Phonetic name';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.dob IS 'DOB of an individual';
-- ddl-end --
COMMENT ON COLUMN regprc.individual_demographic_dedup.gender IS 'Gender of an individual';


