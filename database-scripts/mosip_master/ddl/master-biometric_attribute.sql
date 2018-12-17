-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: biometric_attribute  - Master biometric_attribute list
-- table alias  : bmattr	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.biometric_attribute (
	
		code  character varying(36) not null ,     
	
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		bmtyp_code character varying (36) not null,		-- master.biometric_type.code
		
		lang_code  character varying(3) not null,		-- master.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.biometric_attribute add constraint pk_bmattr_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_bmattr_<col> on master.biometric_attribute (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.biometric_attribute is 'Master biometric_attribute table'
;

