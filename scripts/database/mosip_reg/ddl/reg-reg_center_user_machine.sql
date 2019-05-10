-- create table section -------------------------------------------------
-- schema 		: reg	   			 	- Registration Module
-- table 		: reg_center_user_machine	- MOSIP Registration center, User and Machine mapping
-- table alias  : cntrmusr	

-- table section -------------------------------------------------
create table reg.reg_center_user_machine (

	regcntr_id character varying (10) not null,	-- reg.registration_center.id
	usr_id  character varying (256) not null,	-- reg.user_detail.id
	machine_id character varying (10) not null,	-- reg.machine_master.id

	lang_code 	character varying (3) not null ,		-- master.language.code	
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_user_machine add constraint pk_cntrmusr_usr_id primary key (regcntr_id, usr_id, machine_id)
 ;

