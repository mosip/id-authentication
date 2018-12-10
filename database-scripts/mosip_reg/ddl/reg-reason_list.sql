-- create table section ------------------------------------------------------------
-- schema 		: reg  		- registration schema
-- table 		: reason_list  - reg reason_list list
-- table alias  : rsnlst	
  
-- schemas section -----------------------------------------------------------------
 
-- create schema if reg  schema not exists
create schema if not exists reg
; 

-- table section --------------------------------------------------------------------

	create table reg.reason_list ( 
	
		code character varying(36) not null ,
		
		name  character varying (64) not null ,			
		descr character varying (256) ,
	
		rsncat_code  character varying(36) not null ,    -- reg.reason_category.code
		
		lang_code  character varying(3) not null ,		-- reg.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;

-- keys section ---------------------------------------------------------------------------
alter table reg.reason_list add constraint pk_rsnlst_code primary key (code, rsncat_code, lang_code)
 ;

-- indexes section ------------------------------------------------------------------------
-- create index idx_rsnlst_<col> on reg.reason_list (col)
-- ;

-- comments section -----------------------------------------------------------------------
-- comment on table reg.reason_list is 'reg reason_list table'
-- ;
