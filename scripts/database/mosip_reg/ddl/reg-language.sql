-- create table section --------------------------------------------------------
-- schema 		: reg	- registration schema
-- table 		: language  - reg Reference Language list
-- table alias  : lang	

-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;

-- table section -------------------------------------------------------------------
create table reg.language (

	code character varying(3) not null,   

	name character varying(64) not null, 
	family character varying(64),
	native_name character varying(64),

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.language add constraint pk_lang_code primary key (code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_lang_name on reg.language (name)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.language is 'reg Language table, Language ISO 639 list with code, name and other details will be stored'
-- ;

-- comment on column reg.language.code is 'Primary key, with lang_cd for multi language'
-- ;
