	
-- create table section -----------------------------------------------------------------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: admin_param    - Table to store all admin configuration parameters used across the mosip modules
-- table alias  : admparm

-- schemas section -----------------------------------------------------------------------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section --------------------------------------------------------------------------------------------------------------------
create table master.admin_param (
	
	code 		character varying (36) not null,
	
	name 		character varying (128) not null,
	val 		character varying (512),
	typ 		character varying (128) not null,
	
	lang_code 	character varying (3) not null,			-- master.location.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
		
)
;

-- keys section -----------------------------------------------------------------------------------------------------------------------
alter table master.admin_param add constraint pk_admparm_code primary key (code, lang_code)
 ;

-- indexes section --------------------------------------------------------------------------------------------------------------------
-- create index idx_admparm_<colx> on master.admin_param (<colx>)
-- ;

-- comments section ------------------------------------------------------------------------------------------------------------------- 
comment on table master.admin_param is 'To store admin configuration parameters and their values.'
;
comment on column master.admin_param.val is 'values for admin configuration parameter....'
;
comment on column master.admin_param.typ is 'System parameter, business parameters, configuration parameters, security parameter, schedule parameter....etc.'
;



