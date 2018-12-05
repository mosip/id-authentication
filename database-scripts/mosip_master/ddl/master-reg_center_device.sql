-- create table section -------------------------------------------------
-- schema 		: master	    	- Master reference Module
-- table 		: reg_center_device	- MOSIP Registration center and Device mapping
-- table alias  : cntrdev

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_device (

	regcntr_id 	character varying (36) not null,	-- master.registration_center.id
	device_id 	character varying (36) not null,  	-- master.device_master.id
	
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
 alter table master.reg_center_device add constraint pk_cntrdev_id primary key (regcntr_id, device_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrdev_<colX> on master.reg_center_device (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_device is 'Table to store all MOSIP registration centers and their mapped machines'
;

