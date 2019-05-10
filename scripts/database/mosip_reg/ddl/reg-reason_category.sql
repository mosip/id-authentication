-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: reason_category  - reg reason_category list
-- table alias  : rsncat	

-- table section -------------------------------------------------------------------------------

	create table reg.reason_category (
	
		code   character varying(36) not null , 
		name   character varying (64) not null , 
		descr  character varying (128) , 
		
		lang_code   character varying(3) not null ,	-- reg.language.code
	
		is_active 	boolean not null, 
		cr_by 		character varying (256) not null, 
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes  timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp 

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.reason_category add constraint pk_rsncat_code primary key (code, lang_code)
 ;


