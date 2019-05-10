-- create table section --------------------------------------------------------
-- schema 		: reg  - reg schema
-- table 		: machine_type  - reg machine_type list
-- table alias  : mtyp	
 
-- table section -------------------------------------------------------------------------------

	create table reg.machine_type (
	
		code  character varying(36) not null ,    -- workstation, laptop, ipad, notepad
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	-- master.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.machine_type add constraint pk_mtyp_code primary key (code, lang_code)
 ;


