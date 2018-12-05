-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: app_detail	- MOSIP Application details
-- table alias  : appdtl	
 
-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.app_detail (
	
	id 		character varying (36) not null,   
	
	name 	character varying (64) not null,
	descr 	character varying (256),
	
	lang_code 	character varying (3) not null,   -- master.language.code 
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table master.app_detail add constraint pk_appdtl_id primary key (id)
 ;

-- indexes section -------------------------------------------------
create unique index idx_appdtl_name on master.app_detail (name)
;

-- comments section ------------------------------------------------- 
comment on table master.app_detail is 'List of MOSIP Applications and details'
;

