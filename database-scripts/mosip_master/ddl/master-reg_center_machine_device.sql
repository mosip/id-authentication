-- create table section -------------------------------------------------
-- schema 		: master	   			 	- Master reference Module
-- table 		: reg_center_machine_device	- MOSIP Registration center, machine and device mapping
-- table alias  : cntrmdev	

-- schemas section -------------------------------------------------
 
-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_machine_device (

	regcntr_id character varying (36) not null, -- master.registration_center.id
	machine_id  character varying (36) not null, -- master.machine_master.id
	device_id   character varying (36) not null, -- master.device_master.id

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
 alter table master.reg_center_machine_device add constraint pk_cntrmdev_regcntr_id primary key (regcntr_id, machine_id, device_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrmdev_<colX> on master.reg_center_machine_device (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_machine_device is 'Table to store all MOSIP registration centers, machines and their mapped devices'
;
