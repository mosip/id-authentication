-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: biometric_type  - reg biometric_type list
-- table alias  : bmtyp	
 
-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------------------------------------

	create table reg.biometric_type (
	
		code  character varying(36) not null ,
	
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,  -- reg.language.code
	
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
alter table reg.biometric_type add constraint pk_bmtyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_bmtyp_<col> on reg.biometric_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.biometric_type is 'reg biometric_type table'
-- ;

