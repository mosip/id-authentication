-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: module_detail  - Master module_detail list
-- table alias  : moddtl	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.module_detail (
		id     character varying(36) not null ,
		name   character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null , -- master.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes  timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.module_detail add constraint pk_moddtl_code primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_moddtl_<col> on master.module_detail (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.module_detail is 'Master module detail table'
;

