-- create table section --------------------------------------------------------
-- schema 		: reg	- registration schema
-- table 		: template_type	- reg template type
-- table alias  : tmpltyp	

-- schemas section -------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;
  
-- table section ---------------------------------------------------------------
create table reg.template_type (

	code character varying(36) not null,   	
	
	descr character varying(256) not null,
	lang_code character varying(3) not null,		-- reg.language.code
	
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
alter table reg.template_type add constraint pk_tmpltyp_code primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_tmpltyp_<colX> on reg.template_type (colX )
-- ;

-- comments section -------------------------------------------------------------
-- comment on table reg.template_type is 'reg table used to store all different types of template types'
-- ;
-- comment on column reg.template_type.code is 'Template type code '
-- ;

