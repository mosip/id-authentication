-- create table section -------------------------------------------------
-- schema 		: master	    	- Master reference Module
-- table 		: reg_center_device_h	- HISTORY : MOSIP Registration center and Device mapping
-- table alias  : cntrdev_h

-- schema section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_device_h (

	regcntr_id 	character varying (36) not null,	-- master.registration_center.id
	device_id 	character varying (36) not null,  	-- master.device_master.id
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp,
	
	eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.

)
;

-- keys section -------------------------------------------------
 alter table master.reg_center_device_h add constraint pk_cntrdev_h_id primary key (regcntr_id, device_id, eff_dtimes)
 ;
 
-- indexes section -------------------------------------------------
-- create index idx_cntrdev_h_<colX> on master.reg_center_device_h (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_device_h is 'History table : Table to store all MOSIP registration centers and their mapped machines'
;

