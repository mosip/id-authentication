-- create table section --------------------------------------------------------
-- schema 		: kernel				-- Kernel schema
-- table 		: key_policy_def_h  		-- Key policy defination table keep key validity for keys generated for application
-- table alias  : keypdefh

-- schemas section ---------------------------------------------------------------

-- create schema if kernel schema not exists
create schema if not exists kernel
;

-- table section -------------------------------------------------------------------
create table kernel.key_policy_def_h (
  
	app_id 		character varying(36) not null,		-- master.app_detail , Application id mapped to Application detail table in master databse	
	eff_dtimes timestamp not null,		-- for history record maintenance including the latest record in base table.
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
alter table kernel.key_policy_def_h add constraint pk_keypdefh_id primary key (app_id, eff_dtimes)
 ;
 
-- indexes section -----------------------------------------------------------------------
-- create index idx_keypdefh_<colX> on kernel.key_policy_def_h (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.key_policy_def_h is 'Key policy defination table keep key validity for keys generated for application'
;

comment on column kernel.key_policy_def_h.app_id is 'master.app_detail , Application id mapped to Application detail table in master databse'
;
