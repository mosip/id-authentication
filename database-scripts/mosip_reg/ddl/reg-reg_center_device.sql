-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration Module
-- table 		: reg_center_device		- MOSIP Registration center and Device mapping
-- table alias  : cntrdev

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;

-- table section -------------------------------------------------
create table reg.reg_center_device (

	regcntr_id 	character varying (36) not null,	-- reg.registration_center.id
	device_id 	character varying (36) not null,  	-- reg.device_master.id

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_device add constraint pk_cntrdev_id primary key (regcntr_id, device_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrdev_<colX> on reg.reg_center_device (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table reg.reg_center_device is 'Table to store all MOSIP registration centers and their mapped machines'
;

