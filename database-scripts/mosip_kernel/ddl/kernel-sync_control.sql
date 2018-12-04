-- create table section --------------------------------------------------------
-- schema 		: kernel	    		- Kernel Module
-- table 		: sync_control			- Sync Job control table for successfull sync jobs
-- table alias  : synctrl

-- schemas section -----------------------------------------------------------------------------------------------------------------

-- create schema if kernel schema for Kernel Module is not exists
create schema if not exists kernel
;
 
-- table section -------------------------------------------------------------------------------
create table kernel.sync_control (

		id 		character varying (36) not null,
		
		syncjob_id character varying (36) not null,	 	-- kernel.sync_job_def.id
		
		machine_id 		character varying (36),			-- master.reg_center_machine.(machine_id, reg_cntr_id)
		regcntr_id 		character varying (36),				-- fk to master machine+center mapped table, not directly master tables.
		
		synctrn_id 	character varying (36) not null,	-- kernel.sync_transaction.id

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
alter table kernel.sync_control add constraint pk_synctrl_id primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_sjob_<colX> on kernel.sync_control (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.sync_control is 'kernel sync_control table which hold all sucessfull sync job details'
;

