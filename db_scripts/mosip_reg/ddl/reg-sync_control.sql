-- create table section --------------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: sync_control			- Sync Job control table - ONLY for successfull sync jobs / No entry means, job not run successfull.
-- table alias  : synctrl
 
-- table section -------------------------------------------------------------------------------
create table reg.sync_control (

		id 		character varying (36) not null,
		
		syncjob_id character varying (36) not null,	 	-- reg.sync_job_def.id
		
		machine_id 		character varying (10),			-- reg.reg_center_machine.(machine_id, reg_cntr_id)
		regcntr_id 		character varying (10),				-- fk to master machine+center mapped table, not directly master tables.
		
		synctrn_id 	character varying (36) not null,	-- reg.sync_transaction.id

		last_sync_dtimes timestamp  not null,
		
		lang_code 	character varying (3),				-- master.language.code

		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes  timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.sync_control add constraint pk_synctrl_id primary key (id)
 ;
