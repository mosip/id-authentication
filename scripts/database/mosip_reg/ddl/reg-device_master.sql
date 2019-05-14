-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration Module
-- table 		: device_master		- Master List of device used for registration
-- table alias  : devicem

-- table section -------------------------------------------------
create table reg.device_master (

	id 			character varying (36) not null,

	name 		character varying (64) not null,
	mac_address character varying (64) not null,
	serial_num 	character varying (64) not null,
	ip_address 	character varying (17) , 
	
	validity_end_dtimes timestamp, 

	dspec_id    character varying(36) not null ,   -- master.device_spec.id ,  spec mapped to device_type
		
	lang_code   character varying (3) not null,	-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.device_master add constraint pk_devicem_id primary key (id, lang_code)
 ;


