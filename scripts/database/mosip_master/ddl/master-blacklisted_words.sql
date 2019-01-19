-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: blacklisted_words  - Master blacklisted_words list
-- table alias  : blwrd	
  
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.blacklisted_words (
	
		word  character varying (128) not null ,
		
		descr  character varying (256) ,
		lang_code  character varying(3) not null ,		-- master.language.code
	
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
alter table master.blacklisted_words add constraint pk_blwrd_code primary key (word, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_blwrd_<col> on master.blacklisted_words (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.blacklisted_words is 'Master blacklisted_words table'
;

