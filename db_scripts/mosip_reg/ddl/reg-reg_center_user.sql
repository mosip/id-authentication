-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration schema
-- table 		: reg_center_user	- MOSIP Application users and registration center mapping
-- table alias  : cntrusr
 
-- table section -------------------------------------------------
create table reg.reg_center_user (

	regcntr_id character varying (10) not null,	 -- reg.registration_center.id
	usr_id  character varying (256) not null,	 -- reg.user_detail.id
	
	lang_code 	character varying (3) not null ,		-- master.language.code	
	
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
 alter table reg.reg_center_user add constraint pk_cntrusr_usr_id primary key (regcntr_id, usr_id)
 ;
