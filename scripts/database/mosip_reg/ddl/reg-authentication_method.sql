-- create table section -------------------------------------------------
-- schema 		: reg	    			- Registration client, referenced from master data 
-- table 		: authentication_method		- List of login methods used across the MOSIP applications
-- table alias  : authm	

-- schemas section -------------------------------------------------
 
-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;

-- table section -------------------------------------------------
create table reg.authentication_method (

	code		character varying (36) not null,  
	
	method_seq 	smallint,
	
	lang_code   character varying (3) not null,	  -- master.language.code 
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.authentication_method add constraint pk_authm_code primary key (code, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_logmeth_<colX> on reg.authentication_method (colX )
-- ;

-- comments section ------------------------------------------------- 
-- comment on table reg.authentication_method is 'Table to store list of Authentication methods used across the MOSIP applications'
-- ;

