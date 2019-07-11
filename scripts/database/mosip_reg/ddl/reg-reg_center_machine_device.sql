-- create table section -------------------------------------------------
-- schema 		: reg	   			 		- Registration Module
-- table 		: reg_center_machine_device	- MOSIP Registration center, machine and device mapping
-- table alias  : cntrmdev	


-- table section -------------------------------------------------
create table reg.reg_center_machine_device (

	regcntr_id character varying (10) not null, -- reg.registration_center.id
	machine_id  character varying (10) not null, -- reg.machine_master.id
	device_id   character varying (36) not null, -- reg.device_master.id

	lang_code 	character varying (3) not null ,  -- master.language.code

	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp

)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_machine_device add constraint pk_cntrmdev_cntr_id primary key (regcntr_id, machine_id, device_id)
 ;
