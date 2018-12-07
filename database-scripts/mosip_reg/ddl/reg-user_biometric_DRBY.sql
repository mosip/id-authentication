-- create table section --------------------------------------------------------
-- schema 		: reg 			 	- Registration Client Schema
-- table 		: user_biometric 	- User Biometric details stored at registration client DB for Authentication
-- table alias  : usrbio 
 
-- schemas section ------------ THIS IS FOR DERBY CLIENT LCOAL DATABASE ----------------------

-- create schema if reg schema for registration client not exists
create schema if not exists reg
;
 
-- table section -------------- THIS IS FOR DERBY CLIENT LCOAL DATABASE -------------------

	create table reg.user_biometric (
		usr_id 		character varying (36) not null, -- reg.user_detail.id
		bmtyp_code	character varying(36) not null,	 -- master.biometric_type.code ; BioType : Finger Print, Iris etc 
		bmatt_code  character varying (36) not null, -- master.biometric_attribute.code ; BioAttributes: Lthumb, Rthumb, R index finger, R iris etc 
		bio_raw_image  	blob (1M),
		bio_minutia		blob (1M),
		bio_iso_image  	blob (1M),
		
		quality_score   numeric(5,3) ,
		no_of_retry  	smallint,
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp not null,
		upd_by  	character varying (32),
		upd_dtimes 	timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp
	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.user_biometric add constraint pk_usrbio_bmatt_code primary key (usr_id, bmtyp_code, bmatt_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_usrbio_<colX> on reg.user_biometric (colX)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.user_biometric is 'User Biometric details stored at registration client DB for Authentication'
-- ;

