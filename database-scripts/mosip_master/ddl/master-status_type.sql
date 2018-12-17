-- create table section --------------------------------------------------------
-- schema 		: master 		- Master Reference schema
-- table 		: status_type   - Master status_type list
-- table alias  : sttyp
 
-- schemas section ---------------------------------------------------------------
 
-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.status_type (
	
		code   character varying(36) not null ,   
	
		name   character varying (64) not null ,	-- different types of statuses, for example, transaction status vs registration status
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
alter table master.status_type add constraint pk_sttyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_sttyp_<col> on master.status_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.status_type is 'Master status_type table'
;

