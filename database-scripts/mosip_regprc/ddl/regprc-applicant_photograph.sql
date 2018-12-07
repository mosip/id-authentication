-- create table section -------------------------------------------------
-- schema 		: regprc						- Registration Processor
-- table 		: applicant_photograph			- Registration processor Applicant's photograph details
-- table alias  : applphot	

-- schemas section -------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section -------------------------------------------------
	create table regprc.applicant_photograph (	
	
		reg_id 			character varying(39) not null,		--  regprc.registration.id
		prereg_id  		character varying(64) ,				--  no fk.  data populate.
		
		image_name		character varying(128) not null ,
		quality_score   numeric(5,3) ,
		no_of_retry  	smallint,
		image_store		bytea ,
		
		has_excp_photograph boolean,
		excp_photo_name 	character varying(128) ,
		excp_photo_store	bytea ,
		
		is_active 	boolean not null,
		cr_by 		character varying (32) not null ,
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp
				
	)
;

-- keys section -------------------------------------------------
alter table regprc.applicant_photograph add constraint pk_applphot_reg_id primary key (reg_id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_applphot_<col> on regprc.applicant_photograph ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.applicant_photograph is 'applicant_photograph table stores applicant Photograph details'
;

