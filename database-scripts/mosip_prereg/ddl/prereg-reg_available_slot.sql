-- create table section -------------------------------------------------
-- schema 		: prereg					- Pre Registration
-- table 		: reg_available_slot		- Stores all available slots for appointment booking for registration
-- table alias  : ravlslt			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
	create table prereg.reg_available_slot (
		
		regcntr_id 			character varying (36) not null , -- master.registration_center
		availability_date 	date not null,
		slot_from_time 	time not null,
		slot_to_time 	time,
		availble_kiosks smallint ,
		
		cr_by 		character varying (32) not null,      
		cr_dtimes 	timestamp not null ,
		upd_by 		character varying (32) ,
		upd_dtimes 	timestamp ,
		is_deleted 	boolean ,
		del_dtimes	timestamp
				
	)
;

-- keys section -------------------------------------------------
alter table prereg.reg_available_slot add constraint pk_ravlslt_id primary key (regcntr_id, availability_date, slot_from_time)
;

-- indexes section -------------------------------------------------
-- create unique index idx_ravlslt_<col> on prereg.reg_available_slot ( col )
-- ;

-- comments section ------------------------------------------------- 
comment on table prereg.reg_available_slot is 'reg_available_slot table stores all available slots for appointment booking for registration'
;

