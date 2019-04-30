-- create table section -------------------------------------------------
-- schema 		: prereg								- Pre Registration
-- table 		: applicant_demographic_consumed		- Pre Registration Applicant's demographic details which are consumed
-- table alias  : appldemc			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
	create table prereg.applicant_demographic_consumed (
			
		prereg_id  			character varying(36) not null ,
		
		demog_detail   		bytea not null,
		demog_detail_hash   character varying(64) not null ,
		encrypted_dtimes 	timestamp not null ,

		status_code 		character varying(36) not null ,	-- master.status_list.code
		lang_code  			character varying(3) not null ,		-- master.language.code
		
		cr_appuser_id 		character varying (256) not null, 

		cr_by 		character varying (256) not null,      
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (256) ,
		upd_dtimes 	timestamp
	)
;

-- keys section -------------------------------------------------
alter table prereg.applicant_demographic_consumed add constraint pk_appldemc_prereg_id primary key (prereg_id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_appldemc_<col> on prereg.applicant_demographic_consumed ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table prereg.applicant_demographic_consumed is 'applicant_demographic_consumed table stores applicant demographic details which are consumed'
;

