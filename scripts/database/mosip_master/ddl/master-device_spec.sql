-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: device_spec  - Master device_spec list
-- table alias  : dspec	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
  
-- table section -------------------------------------------------------------------------------

	create table master.device_spec ( 
	
		id character varying(36) not null ,
	
		name  character varying (64) not null ,
		brand character varying(32) not null ,			-- make
		model character varying(16) not null ,
		
		dtyp_code  character varying(36) not null ,  	-- master.device_type.code
		
		min_driver_ver character varying(16) not null ,
		descr character varying (256) ,
		
		lang_code  character varying(3) not null ,	-- master.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.device_spec add constraint pk_dspec_code primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_dspec_<col> on master.device_spec (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.device_spec is 'Master device_spec table'
;
