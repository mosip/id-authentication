-- create table section -------------------------------------------------
-- schema 		: prereg					- Pre Registration
-- table 		: applicant_demographic		- Pre Registration Applicant's demographic details
-- table alias  : appldem			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
	create table prereg.applicant_demographic (
			
		prereg_id  			character varying(36) not null ,
		
		demog_detail   		bytea ,
		demog_detail_hash 	character varying(64) not null ,
		encrypted_dtimes 	timestamp not null ,

		status_code 		character varying(36) not null ,	-- master.status_list.code
		lang_code  			character varying(3) not null ,		-- master.language.code
		
		consumed 			boolean not null, 
		cr_appuser_id 		character varying (64) not null, 

		cr_by 		character varying (32) not null,      
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes 	timestamp
	)
;

-- keys section -------------------------------------------------
alter table prereg.applicant_demographic add constraint pk_appldem_prereg_id primary key (prereg_id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_appldem_<col> on prereg.applicant_demographic ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table prereg.applicant_demographic is 'applicant_demographic table stores applicant demographic details'
;

