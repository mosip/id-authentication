-- create table section --------------------------------------------------------
-- schema 		: reg  - reg  schema
-- table 		: device_type  - reg device_type list
-- table alias  : dtyp	
 
-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------

	create table reg.device_type (
	
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
alter table reg.device_type add constraint pk_dtyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_dtyp_<col> on reg.device_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table reg.device_type is 'reg device_type table'
;

