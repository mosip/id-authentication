-- create table section --------------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: sync_control			- Sync Job control table - ONLY for successfull sync jobs / No entry means, job not run successfull.
-- table alias  : synctrl

-- schema section -----------------------------------------------------------------------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------
create table reg.sync_control (

		id 		character varying (36) not null,
		
		syncjob_id character varying (36) not null,	 	-- reg.sync_job_def.id
		
		machine_id 		character varying (36),			-- reg.reg_center_machine.(machine_id, reg_cntr_id)
		regcntr_id 		character varying (36),				-- fk to master machine+center mapped table, not directly master tables.
		
		synctrn_id 	character varying (36) not null,	-- reg.sync_transaction.id

		last_sync_dtimes timestamp  not null,
		
		lang_code 	character varying (3),				-- master.language.code

		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes  timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.sync_control add constraint pk_synctrl_id primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_sjob_<colX> on reg.sync_control (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table reg.sync_control is 'Registration sync_control table which hold all sucessfull sync job details'
;

