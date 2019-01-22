-- create table section --------------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: sync_job_def  		- Job defination for sync process between client and server
-- table alias  : syncjob

-- schema section -----------------------------------------------------------------------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;

-- table section -------------------------------------------------------------------------------

create table reg.sync_job_def (
				
		id 		character varying (36) not null,
		
		name 				character varying (64) not null,
		api_name 			character varying (64),
		parent_syncjob_id  	character varying (36),		-- self reference, reg.sync_job_def.id
		sync_freq 			character varying (36),
		lock_duration 		character varying (36),
		
		lang_code 	character varying (3),		-- master.language.code
		
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes 	timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.sync_job_def add constraint pk_syncjob_id primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
create unique index idx_syncjob_name on reg.sync_job_def (name)
;

-- comments section -------------------------------------------------------------------------- 
comment on table reg.sync_job_def is 'Registration sync_job_def table which hold all sync job details'
;

