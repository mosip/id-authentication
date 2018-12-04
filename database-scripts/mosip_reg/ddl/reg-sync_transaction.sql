-- create table section --------------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: sync_transaction 		- Sync Job and Transaction details
-- table alias  : synctrn

-- schemas section -----------------------------------------------------------------------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------

create table reg.sync_transaction (
				
		id 				character varying (36) not null,
		
		syncjob_id 		character varying (36) not null,    -- reg.sync_job_def.id
		sync_dtimes		timestamp not null,
		
		status_code 	character varying(36) not null,		-- reg.status_list.code
		status_comment 	character varying(256),
		
		trigger_point 	character varying (32),
		sync_from 		character varying (32),
		sync_to 		character varying (32),
		
		machine_id 		character varying (36),			--reg.reg_center_machine.(machine_id, reg_cntr_id)
		regcntr_id 		character varying (36),				-- fk to master machine+center mapped table, not directly master tables.

		ref_id_type 	character varying(64),				-- master.id_type.code		
		ref_id 		character varying (64),

		sync_param 	character varying(2048), 			-- This column follow json structure store for param key value pair 

		lang_code 	character varying (3),				-- master.language.code

		-- is_active 	boolean not null,

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes  timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.sync_transaction add constraint pk_synctrn_id primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_synctrn_<colX> on reg.sync_transaction (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table reg.sync_transaction is 'Master sync_transaction table which hold all sync job details and transactions'
;

