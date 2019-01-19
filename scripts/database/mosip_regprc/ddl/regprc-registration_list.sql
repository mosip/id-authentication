-- create table section ---------------------------------------------------
-- schema 		: regprc			- Registration Processor (enrolment server or ID issuance server)
-- table 		: registration_list - List of registration packets synched from registration client to processor
-- table alias  : reglist

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;
 
-- table section ---------------------------------------------------------
create table regprc.registration_list (

	id character varying(36) not null,

	reg_id character varying(39) not null,			-- no fk.  data populate.
	reg_type character varying(64),					-- no fk.  data populate.
	parent_reg_id character varying(39),			-- no fk.  data populate.

	status_code character varying(36),				-- no fk.  data populate.
	status_comment character varying(256),

	lang_code character varying(3) not null,		-- no fk.  data populate.
	
	is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table regprc.registration_list add constraint pk_reglist_id primary key (id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_reglist_<colX> on regprc.registration_list (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.registration_list is 'List of Registration to Process'
;

