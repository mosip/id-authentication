	
-- create table section -----------------------------------------------------------------------------------------------------------
-- schema 		: reg 				- Registration schema
-- table 		: global_param   	- Templates to store all global configuration parameters used in registration client module
-- table alias  : glbparm

-- table section --------------------------------------------------------------------------------------------------------------------
create table reg.global_param (

	code 	character varying (128) not null,
	
	name 	character varying (128) not null,
	val 	character varying (512),
	typ		character varying (128) not null,
	
	lang_code character varying (3) not null,	-- master.location.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
		
)
;

-- keys section -----------------------------------------------------------------------------------------------------------------------
alter table reg.global_param add constraint pk_glbparm_code primary key (code, lang_code)
 ;




