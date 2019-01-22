-- create table section -------------------------------------------------
-- schema 		: regprc						- Registration Processor
-- table 		: biometric_exception			- Registration processor Applicant's Biometric Exception details
-- table alias  : bioexcp
 
-- schemas section -------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section -------------------------------------------------
	create table regprc.biometric_exception (	
	
		reg_id 			character varying(39) not null ,	--  regprc.registration.id
		prereg_id  		character varying(64) ,				--  no fk.  data populate.
		
		bio_typ 		character varying(64) not null ,
		missing_bio 	character varying(64) not null ,
		excp_descr		character varying(256) ,
		excp_typ		character varying(64) ,
			
		status_code 	character varying(36) not null,		-- no fk.  data popluate
		lang_code  		character varying(3) not null ,		-- no fk.  data popluate
		
		cr_by 		character varying (32) not null ,
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp
				
	)
;

-- keys section -------------------------------------------------
alter table regprc.biometric_exception add constraint pk_bioexcp_reg_id primary key (reg_id, missing_bio)
;

-- indexes section -------------------------------------------------
-- create unique index idx_bioexcp_<col> on regprc.biometric_exception ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.biometric_exception is 'biometric_exception table stores applicant exceptions'
;

