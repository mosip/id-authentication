-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: device_type  - Master device_type list
-- table alias  : dtyp	
 
-- schemas section ---------------------------------------------------------------
 
-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.device_type (
	
		code  character varying(36) not null ,   --  iris, biometric, scanner, etc.
	
		name   character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	-- master.language.code
	
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
alter table master.device_type add constraint pk_dtyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_dtyp_<col> on master.device_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.device_type is 'Master device_type table'
;

