-- create table section --------------------------------------------------------
-- schema 		: reg	- registration schema
-- table 		: language  - reg Reference Language list
-- table alias  : lang	

-- table section -------------------------------------------------------------------
create table reg.language (

	code character varying(3) not null,   

	name character varying(64) not null, 
	family character varying(64),
	native_name character varying(64),

	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.language add constraint pk_lang_code primary key (code)
 ;
