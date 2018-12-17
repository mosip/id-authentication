	
-- create table section -----------------------------------------------------------------------------------------------------------
-- schema 		: master		  - Master Reference schema
-- table 		: global_param    - Templates to store all global configuration parameters used across the mosip modules
-- table alias  : glbparm

-- schemas section -----------------------------------------------------------------------------------------------------------------

-- create schema if master schema for Master reference tables is not exists
create schema if not exists master
;

-- table section --------------------------------------------------------------------------------------------------------------------
create table master.global_param (

	code 	character varying (36) not null,
	
	name 	character varying (128) not null,
	val 	character varying (512),
	typ		character varying (128) not null,
	
	lang_code character varying (3) not null,	-- master.location.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
		
)
;

-- keys section -----------------------------------------------------------------------------------------------------------------------
alter table master.global_param add constraint pk_glbparm_code primary key (code, lang_code)
 ;

-- indexes section --------------------------------------------------------------------------------------------------------------------
-- create index idx_glbparm_<colx> on master.global_param (<colx>)
-- ;

-- comments section ------------------------------------------------------------------------------------------------------------------- 
comment on table master.global_param is 'To store global configuration parameters and their values.'
;
comment on column master.global_param.val is 'values for global configuration parameter....'
;
comment on column master.global_param.typ is 'System parameter, business parameters, configuration parameters....etc.'
;



