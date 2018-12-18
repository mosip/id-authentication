-- create table section -------------------------------------------------
-- schema 		: reg	    - registration schema
-- table 		: role_list	 - List of roles used across MOSIP Modules
-- table alias  : rolelst		

-- schemas section -------------------------------------------------
 
-- create schema if reg schema for reg reference Module is not exists
create schema if not exists reg
;

-- table section -------------------------------------------------
create table reg.role_list (
	
	code 		character varying (36) not null,
	
	descr 		character varying (256),
	
	lang_code 	character varying (3) not null,		-- reg.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.role_list add constraint pk_rolelst_code primary key (code, lang_code)
 ;

-- indexes section -------------------------------------------------
-- create index idx_rolelst_<colX> on reg.role_list (colX )
-- ;

-- comments section ------------------------------------------------- 
-- comment on table reg.role_list is 'Table to store list of roles like registration officer, supervisor, operator...etc which is used across the MOSIP Modules'
-- ;
