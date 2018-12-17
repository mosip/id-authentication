-- create table section ------------------------------------------------------------
-- schema 		: reg  - reg  schema
-- table 		: machine_spec  - reg machine_spec list
-- table alias  : mspec	
 
-- schemas section -----------------------------------------------------------------

-- create schema if regschema not exists
create schema if not exists reg
;

-- table section --------------------------------------------------------------------

	create table reg.machine_spec ( 
	
		id character varying(36) not null ,
		
		name  character varying (64) not null ,			
		brand character varying(32) not null ,			-- make
		model character varying(16) not null ,
		
		mtyp_code  character varying(36) not null ,     -- master.machine_type.code
		
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

-- keys section ---------------------------------------------------------------------------
alter table reg.machine_spec add constraint pk_mspec_code primary key (id)
 ;

-- indexes section ------------------------------------------------------------------------
-- create index idx_mspec_<col> on reg.machine_spec (col)
-- ;

-- comments section -----------------------------------------------------------------------
comment on table reg.machine_spec is 'reg machine_spec table'
;
