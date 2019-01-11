-- create table section -------------------------------------------------
-- schema 		: master	    			-- Master reference Module
-- table 		: app_role_priority			-- List of application, process, role and their priority
-- table alias  : roleprt

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;
  
-- table section -------------------------------------------------
create table master.app_role_priority (
	
	app_id 			  	character varying (36) not null,  	-- master.app_detail.id
	process_id 			character varying (36) not null,    -- master.process_list.id -- login auth, packet auth, exception auth, eod auth, MV authentication..etc
	role_code 			character varying (36) not null,  	-- master.role_list.code
	priority 			smallint,
	
	lang_code  		character varying(3) not null ,     	-- master.language.code
	
	is_active 		boolean not null,
	cr_by 			character varying (32) not null,
	cr_dtimes		timestamp not null,
	upd_by  		character varying (32),
	upd_dtimes 		timestamp,
	is_deleted 		boolean,
	del_dtimes 		timestamp

)
;

-- keys section -------------------------------------------------
 alter table master.app_role_priority add constraint pk_roleprt_id primary key (app_id, process_id, role_code)
 ;

 -- indexes section -------------------------------------------------
create unique index uk_roleprt_id on master.app_role_priority (app_id, process_id, priority) 
;

-- indexes section -------------------------------------------------
-- create index idx_roleprt_<colX> on master.app_role_priority (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.app_role_priority is 'Table to store all MOSIP Application, application process, role and their priority'
;

