-- create table section -------------------------------------------------
-- schema 		: reg	    - registration schema
-- table 		: reg_center_type	- Registration centers types
-- table alias  : cntrtyp

-- table section -------------------------------------------------
create table reg.reg_center_type (

	code character varying (36) not null,

	name character varying (64) not null,
	descr character varying (128),
	
	lang_code character varying (3) not null,	-- reg.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_type add constraint pk_cntrtyp_id primary key (code, lang_code)
 ;

