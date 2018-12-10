-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: registration_center_h  - HISTORY :  Master registration_center_h list
-- table alias  : regcntr_h	

-- schema section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

create table master.registration_center_h (
		 
		id character varying (36) not null ,			
		name  character varying(128) not null ,
		
		cntrtyp_code character varying (36) ,			-- master.reg_center_type.code
			
		addr_line1 character varying(256) ,
		addr_line2 character varying(256) ,
		addr_line3 character varying(256) ,
		
		latitude  	character varying(32) ,
		longitude 	character varying(32) ,
				
		location_code character varying(36) not null ,	-- master.location.code
		
		contact_phone  character varying (16),
		contact_person character varying (128), 
		
		number_of_kiosks smallint ,
		working_hours  character varying (32) ,
		per_kiosk_process_time time,   
		center_start_time time,
		center_end_time time,
		lunch_start_time time,
		lunch_end_time time,
		time_zone character varying (64),

		holiday_loc_code  character varying(36),  		-- master.location.code ->> master.loc_holiday.location_code
									-- tag holiday list specific to location.code / country,state,city,etc
		
		lang_code  character varying(3) not null ,  	-- master.language.code
		
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp,
	
		eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.


	)
;

-- keys section -------------------------------------------------------------------------------
alter table master.registration_center_h add constraint pk_regcntr_h_code primary key (id, eff_dtimes)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_regcntr_h_name on master.registration_center_h (name)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.registration_center_h is 'History table : Master registration_center_h table'
;

