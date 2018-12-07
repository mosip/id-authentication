-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: title  - Master title list
-- table alias  : ttl	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.title (
	
		code  character varying(16) not null ,
	
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	-- master.language.code
	
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
alter table master.title add constraint pk_ttl_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_ttl_<col> on master.title (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.title is 'Master title table'
;

