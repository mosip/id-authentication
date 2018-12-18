-- create table section --------------------------------------------------------
-- schema 		: master	- Master Reference schema
-- table 		: template_type	- Master template type
-- table alias  : tmpltyp	

-- schemas section -------------------------------------------------------------

-- create schema if Master reference schema not exists
create schema if not exists master
;
  
-- table section ---------------------------------------------------------------
create table master.template_type (

	code character varying(36) not null,   	
	
	descr character varying(256) not null,
	lang_code character varying(3) not null,		-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted	boolean,
	del_dtimes 	timestamp

)
;

-- keys section ------------------------------------------------------------------
alter table master.template_type add constraint pk_tmpltyp_code primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_tmpltyp_<colX> on master.template_type (colX )
-- ;

-- comments section -------------------------------------------------------------
comment on table master.template_type is 'Master table used to store all different types of template types'
;
comment on column master.template_type.code is 'Template type code '
;

