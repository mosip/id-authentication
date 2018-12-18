-- create table section -------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: reg_center_user_machine	- MOSIP Registration center, User and Machine mapping
-- table alias  : cntrmusr	

-- schemas section -------------------------------------------------
 
-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;

-- table section -------------------------------------------------
create table reg.reg_center_user_machine (

	regcntr_id character varying (36) not null,	-- reg.registration_center.id
	usr_id  character varying (36) not null,	-- reg.user_detail.id
	machine_id character varying (36) not null,	-- reg.machine_master.id

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_user_machine add constraint pk_cntrmusr_usr_id primary key (regcntr_id, usr_id, machine_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrmusr_<colX> on reg.reg_center_user_machine (colX )
-- ;

-- comments section ------------------------------------------------- 
-- comment on table reg.reg_center_user_machine is 'Table to store all MOSIP registration centers, Users and their mapped machines'
-- ;

