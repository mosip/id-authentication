-- create table section --------------------------------------------------------
-- schema 		: kernel				-- Kernel schema
-- table 		: key_policy_def  		-- Key policy defination table keep key validity for keys generated for application
-- table alias  : keypdef

-- schemas section ---------------------------------------------------------------

-- create schema if kernel schema not exists
create schema if not exists kernel
;

-- table section -------------------------------------------------------------------
create table kernel.key_policy_def (
  
	app_id 		character varying(36) not null,		-- master.app_detail , Application id mapped to Application detail table in master databse	
	key_validity_duration smallint, 				-- Duration in days for key validity.
	
	is_active	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table kernel.key_policy_def add constraint pk_keypdef_id primary key (app_id)
 ;
 
-- indexes section -----------------------------------------------------------------------
-- create index idx_keypdef_<colX> on kernel.key_policy_def (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.key_policy_def is 'Key policy defination table keep key validity for keys generated for application'
;

comment on column kernel.key_policy_def.app_id is 'master.app_detail , Application id mapped to Application detail table in master databse'
;
