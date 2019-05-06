-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: gender  - reg Gender list
-- table alias  : gndr	
  
-- table section -------------------------------------------------------------------------------

create table reg.gender (

	code 		character (16) not null,     

	name 		character varying(64) not null,
	

	lang_code  	character varying(3) not null,		-- reg.language.code

	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp ,
	is_deleted 	boolean,
	del_dtimes	timestamp 
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.gender add constraint pk_gndr_code primary key (code, lang_code)
 ;

