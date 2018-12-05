-- create table section --------------------------------------------------------
-- schema 		: master  	- Master Reference schema
-- table 		: location  - Master location list
-- table alias  : loc	
 
-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.location (
	
		code character varying (36) not null , 
		name character varying (128) not null ,
		
		hierarchy_level smallint not null ,
		hierarchy_level_name character varying (64) not null ,
		
		parent_loc_code character varying (32) ,        -- self joining,  master.location.code
		
		lang_code  character varying(3) not null ,		-- master.language.code

		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 

		)
	;
		
	--  Below is sample data for understanding the hierarchy_level and data to be populated.
	--  code(unique,pkey)	name  			level	levelname	parent code 	

	--  IND				INDIA			0		COUNTRY		NULL	

	-- 	KAR				KARNATAKA		1		STATE		IND	
	--  TN				TAMILNADU		1		STATE		IND
	--  KL				KERALA			1		STATE		IND

	-- 	BLR				BANGALURU		2		CITY		KAR	
	-- 	MLR	 			MANGALORE		2		CITY		KAR	
	-- 	MSR				MYSURU			2		CITY		KAR	
	-- 	KLR				KOLAR			2		CITY		KAR

	-- 	CHNN			CHENNAI 		2		CITY		TN
	-- 	CBE				COIMBATORE		2		CITY		TN			

	--  RRN				RRNAGAR			3		AREA		BLR
	--  560029			560029			4		ZIPCODE		RRN	
	-- 								(  for pin/zip, both code and name can be same)
			
	--  600001			600001			3		ZIPCODE		CHN		


-- keys section -------------------------------------------------------------------------------
alter table master.location add constraint pk_loc_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_loc_name on master.location (name)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.location is 'Master location table'
;

