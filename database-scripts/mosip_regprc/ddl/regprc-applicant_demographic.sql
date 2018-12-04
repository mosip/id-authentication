-- create table section -------------------------------------------------
-- schema 		: regprc						- Registration Processor
-- table 		: applicant_demographic			- Registration processor Applicant's demographic details
-- table alias  : appldem			

-- schemas section -------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section -------------------------------------------------
	create table regprc.applicant_demographic (
			
		reg_id 			character varying(39) not null,		-- regprc.registration.id
		prereg_id  		character varying(64) ,		-- only populate, no fk. 
			
		demog_detail   bytea ,
		
		status_code 	character varying(36) not null ,	-- master.status_list.code
		lang_code  		character varying(3) not null ,		-- master.language.code
		
		is_active 	boolean not null,
		cr_by 		character varying (32) not null ,
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes 	timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------
alter table regprc.applicant_demographic add constraint pk_appldem_reg_id primary key (reg_id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_appldem_<col> on regprc.applicant_demographic ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.applicant_demographic is 'applicant_demographic table stores applicant demographic details'
;

