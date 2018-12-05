-- create table section --------------------------------------------------------
-- schema 		: reg  - reg schema
-- table 		: machine_type  - reg machine_type list
-- table alias  : mtyp	
 
-- schemas section ---------------------------------------------------------------

-- create schema if reg schema not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------

	create table reg.machine_type (
	
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
alter table reg.machine_type add constraint pk_mtyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_mtyp_<col> on reg.machine_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table reg.machine_type is 'reg machine_type table'
;

