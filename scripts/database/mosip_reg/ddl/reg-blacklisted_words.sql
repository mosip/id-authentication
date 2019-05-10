-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: blacklisted_words  - reg blacklisted_words list
-- table alias  : blwrd	

-- table section -------------------------------------------------------------------------------

	create table reg.blacklisted_words (
	
		word  character varying (128) not null ,
		
		descr  character varying (256) ,
		lang_code  character varying(3) not null ,		-- reg.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes 	timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.blacklisted_words add constraint pk_blwrd_code primary key (word, lang_code)
 ;