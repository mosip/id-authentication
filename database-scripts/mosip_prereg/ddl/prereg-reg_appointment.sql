-- create table section -------------------------------------------------
-- schema 		: prereg					- Pre Registration
-- table 		: reg_appointment			- Stores all booked slots for registration
-- table alias  : rappmnt			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
	create table prereg.reg_appointment (
		
		id 					character varying (36) not null ,
		regcntr_id 			character varying (36) not null ,	 -- master.registration_center
		prereg_id 			character varying(36) not null ,     -- prereg.applicant_demographic.prereg_id
		booking_dtimes		timestamp not null,
		appointment_date 	date,
		slot_from_time 	time,
		slot_to_time 	time,
		status_code 	character varying(36) not null ,	-- master.status_list.code
		
		lang_code  		character varying(3) not null ,		-- master.language.code
		
		cr_by 		character varying (32) not null,      
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes 	timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp
				
	)
;

-- keys section -------------------------------------------------
alter table prereg.reg_appointment add constraint pk_rappmnt_id primary key (id)
;

-- indexes section -------------------------------------------------
-- create unique index idx_rappmnt_<col> on prereg.reg_appointment ( <colX> )
-- ;

-- comments section ------------------------------------------------- 
comment on table prereg.reg_appointment is 'reg_appointment table stores all booked slots for registration'
;

