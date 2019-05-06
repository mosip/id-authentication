-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration Module
-- table 		: reg_center_device		- MOSIP Registration center and Device mapping
-- table alias  : cntrdev

-- table section -------------------------------------------------
create table reg.reg_center_device (

	regcntr_id 	character varying (10) not null,	-- reg.registration_center.id
	device_id 	character varying (36) not null,  	-- reg.device_master.id
	
	lang_code 	character varying (3) not null ,		-- master.language.code	
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_device add constraint pk_cntrdev_id primary key (regcntr_id, device_id)
 ;


