-- create table section -------------------------------------------------
-- schema 		: master  					- Master Reference schema
-- table 		: reg_center_user_machine	- MOSIP Registration center, User and Machine mapping
-- table alias  : cntrmusr	

-- schemas section -------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_user_machine (

	regcntr_id  character varying (36) not null,	-- master.registration_center.id
	usr_id  	character varying (36) not null,	-- master.user_detail.id
	machine_id  character varying (36) not null,	-- master.machine_master.id
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp
)
;

-- keys section -------------------------------------------------
 alter table master.reg_center_user_machine add constraint pk_cntrmusr_usr_id primary key (regcntr_id, usr_id, machine_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrmusr_<colX> on master.reg_center_user_machine (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_user_machine is 'Table to store all MOSIP registration centers, Users and their mapped machines'
;

