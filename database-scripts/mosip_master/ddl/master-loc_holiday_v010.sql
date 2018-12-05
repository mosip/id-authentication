-- create table section --------------------------------------------------------
-- schema 		: master  		- Master Reference schema
-- table 		: loc_holiday  	- Master loc_holiday list
-- table alias  : lochol	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.loc_holiday (
		
		id integer not null,
		location_code character varying(36) not null, 	-- master.location.code
		holiday_date date not null,
		
		holiday_name character varying (64) not null , 
		holiday_desc character varying (128) ,
			
		lang_code  character varying(3) not null ,		-- master.language.code

		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes  timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
			
	)
;

-- keys section -------------------------------------------------------------------------------
alter table master.loc_holiday add constraint pk_lochol_id primary key (location_code, holiday_date, holiday_name, lang_code)
 ;

-- comments section ---------------------------------------------------------------------------
comment on table master.loc_holiday is 'Master loc_holiday table'
;
