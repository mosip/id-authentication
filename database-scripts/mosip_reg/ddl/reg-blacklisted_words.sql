-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: blacklisted_words  - reg blacklisted_words list
-- table alias  : blwrd	
  
-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;

-- table section -------------------------------------------------------------------------------

	create table reg.blacklisted_words (
	
		word  character varying (128) not null ,
		
		descr  character varying (256) ,
		lang_code  character varying(3) not null ,		-- reg.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes 	timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.blacklisted_words add constraint pk_blwrd_code primary key (word, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_blwrd_<col> on reg.blacklisted_words (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.blacklisted_words is 'reg blacklisted_words table'
-- ;

