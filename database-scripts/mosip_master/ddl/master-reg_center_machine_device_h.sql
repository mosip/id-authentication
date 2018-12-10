-- create table section -------------------------------------------------
-- schema 		: master	   			 		- Master reference Module
-- table 		: reg_center_machine_device_h	- HISTORY :  MOSIP Registration center, User and device mapping
-- table alias  : cntrmdev_h	

-- schemas section -------------------------------------------------
 
-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_machine_device_h (

	regcntr_id character varying (36) not null, -- master.registration_center.id
	machine_id  character varying (36) not null, -- master.machine_master.id
	device_id   character varying (36) not null, -- master.device_master.id

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp,
	
	eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.
	
)
;

-- keys section -------------------------------------------------
 alter table master.reg_center_machine_device_h add constraint pk_cntrmdev_h_cntr_id primary key (regcntr_id, machine_id, device_id, eff_dtimes)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrmdev_h_<colX> on master.reg_center_machine_device_h (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_machine_device_h is 'History table :  Table to store all MOSIP registration centers, machines and their mapped devices'
;
