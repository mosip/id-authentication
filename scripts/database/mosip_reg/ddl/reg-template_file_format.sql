-- create table section --------------------------------------------------------
-- schema 		: reg	    			- Registration schema
-- table 		: template_file_format	- Registration template file format
-- table alias  : tffmt
 
 
-- table section ---------------------------------------------------------------
create table reg.template_file_format (

	code character varying(36) not null,
	
	descr character varying(256) not null,
	lang_code character varying(3) not null,		-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section ------------------------------------------------------------------
alter table reg.template_file_format add constraint pk_tffmt_code primary key (code, lang_code)
 ;


