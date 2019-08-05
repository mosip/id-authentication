-- create table section -------------------------------------------------
-- schema 		: reg	    - registration schema
-- table 		: role_list	 - List of roles used across MOSIP Modules
-- table alias  : rolelst		

-- table section -------------------------------------------------
create table reg.role_list (
	
	code 		character varying (36) not null,
	
	descr 		character varying (256),
	
	lang_code 	character varying (3) not null,		-- reg.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.role_list add constraint pk_rolelst_code primary key (code, lang_code)
 ;
