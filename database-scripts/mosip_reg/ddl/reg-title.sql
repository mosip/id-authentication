-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: title  - reg title list
-- table alias  : ttl	
 
-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------

	create table reg.title (
	
		code  character varying(16) not null ,
	
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	-- reg.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.title add constraint pk_ttl_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_ttl_<col> on reg.title (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.title is 'reg title table'
-- ;

