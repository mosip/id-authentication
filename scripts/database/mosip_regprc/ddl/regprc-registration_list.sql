-- create table section ---------------------------------------------------
-- schema 		: regprc			- Registration Processor (enrolment server or ID issuance server)
-- table 		: registration_list - List of registration packets synched from registration client to processor
-- table alias  : reglist

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;
-- NOTE: the code below contains the SQL for the selected object
-- as well for its dependencies and children (if applicable).
-- 
-- This feature is only a convinience in order to permit you to test
-- the whole object's SQL definition at once.
-- 
-- When exporting or generating the SQL for the whole database model
-- all objects will be placed at their original positions.


-- object: regprc.registration_list | type: TABLE --
-- DROP TABLE IF EXISTS regprc.registration_list CASCADE;
CREATE TABLE regprc.registration_list(
	id character varying(36) NOT NULL,
	reg_id character varying(39) NOT NULL,
	reg_type character varying(64),
	packet_checksum character varying(128) NOT NULL,
	packet_size bigint NOT NULL,
	client_status_code character varying(36),
	client_status_comment character varying(256),
	additional_info bytea,
	status_code character varying(36),
	status_comment character varying(256),
	lang_code character varying(3) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_reglist_id PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE regprc.registration_list IS 'List of Registration to Process';
-- ddl-end --
COMMENT ON COLUMN regprc.registration_list.additional_info IS 'Additional Information: Futuristic field to capture any additional information shared by registration client application. The information can be stored in json format as flat structure.';
-- ddl-end --
ALTER TABLE regprc.registration_list OWNER TO sysadmin;
-- ddl-end --


