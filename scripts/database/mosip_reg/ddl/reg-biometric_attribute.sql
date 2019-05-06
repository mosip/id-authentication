-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: biometric_attribute  - reg biometric_attribute list
-- table alias  : bmattr	
 

-- table section -------------------------------------------------------------------------------

	create table reg.biometric_attribute (
	
		code  character varying(36) not null ,     
	
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		bmtyp_code character varying (36) not null,		-- reg.biometric_type.code
		
		lang_code  character varying(3) not null,		-- reg.language.code
	
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
alter table reg.biometric_attribute add constraint pk_bmattr_code primary key (code, lang_code)
 ;
