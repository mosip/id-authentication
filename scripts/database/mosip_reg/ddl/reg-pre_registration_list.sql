-- create table section ---------------------------------------------------
-- schema 		: reg				    - Registration Module
-- table 		: pre_registration_list - List of pre registration packets needs to be synched from pre registration module to registration module
-- table alias  : preregl

-- table section ---------------------------------------------------------
create table reg.pre_registration_list (

	id 			character varying(36) not null,
	prereg_id 	character varying(64) not null,		-- no fk.  data populate.
	prereg_type character varying(64),				-- no fk.  data populate.
	parent_prereg_id character varying(64),			-- no fk.  data populate.
	
	appointment_date 	 date,
	packet_symmetric_key character varying(256),

	status_code character varying(36),				-- no fk.  data populate.
	status_comment character varying(256),			-- no fk.  data populate.
	
	packet_path character varying(256),

	sjob_id character varying(36),					-- no fk.  data populate.
	synctrn_id character varying (36),				-- no fk.  data populate.
	last_upd_dtimes timestamp,                      -- Last updated date time of pre registration

	lang_code character varying(3) not null,		-- no fk.  data populate.

	is_active boolean not null,
	cr_by character varying (256) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.pre_registration_list add constraint pk_preregl_id primary key (id)
 ;


