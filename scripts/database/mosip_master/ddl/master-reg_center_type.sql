-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: reg_center_type	- Registration centers types
-- table alias  : cntrtyp

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.reg_center_type (

	code character varying (36) not null,

	name character varying (64) not null,
	descr character varying (128),
	
	lang_code character varying (3) not null,	-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table master.reg_center_type add constraint pk_cntrtyp_id primary key (code, lang_code)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrtyp_<colX> on master.reg_center_type (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.reg_center_type is 'Table to store registration center type code'
;
