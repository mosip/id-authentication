-- create table section -------------------------------------------------
-- schema 		: prereg							- Pre Registration
-- table 		: reg_appointment_consumed			- Stores all booked slots for registration which are consumed
-- table alias  : rappmntc			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
	create table prereg.reg_appointment_consumed (
		
		id 					character varying (36) not null ,
		regcntr_id 			character varying (10) not null ,	 -- master.registration_center
		prereg_id 			character varying(36) not null ,     -- prereg.applicant_demographic.prereg_id
		booking_dtimes		timestamp not null,
		appointment_date 	date,
		slot_from_time 		time,
		slot_to_time 		time,
		
		lang_code  	character varying(3) not null ,		-- master.language.code
		
		cr_by 		character varying (256) not null,      
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (256) ,
		upd_dtimes 	timestamp
	)
;

-- keys section -------------------------------------------------
alter table prereg.reg_appointment_consumed add constraint pk_rappmntc_id primary key (id)
;

alter table prereg.reg_appointment_consumed add constraint uk_rappmntc_id unique (prereg_id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_rappmntc_<col> on prereg.reg_appointment_consumed ( <colX> )
-- ;

-- comments section ------------------------------------------------- 
comment on table prereg.reg_appointment_consumed is 'reg_appointment_consumed table stores all booked slots for registration which areconsumed'
;

