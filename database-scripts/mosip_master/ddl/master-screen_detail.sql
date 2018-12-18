-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: screen_detail	- MOSIP Application Screens details
-- table alias  : scrdtl	

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.screen_detail (
	id 		character varying (36) not null,

	app_id 	character varying (36) not null,	-- master.app_detail.id

	name 	character varying (64) not null,
	descr 	character varying (256),
	
	lang_code 	character varying (3) not null,	-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
)
;

-- keys section -------------------------------------------------
 alter table master.screen_detail add constraint pk_scrdtl_id primary key (id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_scrdtl_name on master.screen_detail (name)
-- ;

-- comments section ------------------------------------------------- 
comment on table master.screen_detail is 'List of MOSIP Applications Screen and details'
;

