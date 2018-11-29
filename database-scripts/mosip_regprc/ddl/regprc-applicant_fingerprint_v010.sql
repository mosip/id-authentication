-- create table section -------------------------------------------------
-- schema 		: regprc						- Registration Processor
-- table 		: applicant_fingerprint			- Registration processor Applicant's fingerprint details
-- table alias  : appfprnt	

-- schemas section -------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;
 
-- table section -------------------------------------------------
	create table regprc.applicant_fingerprint (
			
		reg_id 			character varying(28) not null,		--  regprc.registration.id
		prereg_id  		character varying(64)  ,			--  no fk.  data populate.
		image_name		character varying(128) not null ,	
		typ				character varying(36) not null ,
		quality_score   numeric(5,3) ,
		no_of_retry  	smallint,
		forced_capture boolean,
			
		is_active 	boolean not null,
		cr_by 		character varying (32) not null ,
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes  timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp		
	)
;

-- keys section -------------------------------------------------
alter table regprc.applicant_fingerprint add constraint pk_appfprnt_reg_id primary key (reg_id, typ)
;


-- indexes section -------------------------------------------------
-- create unique index idx_appfprnt_<col> on regprc.applicant_fingerprint ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.applicant_fingerprint is 'applicant_fingerprint table stores applicant fingerprint details'
;
