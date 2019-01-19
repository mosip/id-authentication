-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: machine_type  - Master machine_type list
-- table alias  : mtyp	
   
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.machine_type (
	
		code  character varying(36) not null ,    -- workstation, laptop, ipad, notepad
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
alter table master.machine_type add constraint pk_mtyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_mtyp_<col> on master.machine_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.machine_type is 'Master machine_type table'
;

