-- create table section --------------------------------------------------------
-- schema 		: reg 			- Registration schema
-- table 		: individual_type  	- Individual type table
-- table alias  : indvtyp	

-- table section -------------------------------------------------------------------------------

create table reg.individual_type (
	code 		character varying(36) not null,     
	name		character varying(64) not null,

	lang_code  	character varying(3) not null,		-- master.language.code

	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp ,
	is_deleted 	boolean,
	del_dtimes	timestamp 
)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.individual_type add constraint pk_indvtyp_code primary key (code, lang_code);
