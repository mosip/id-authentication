-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: authentication_method		- List of login methods used across the MOSIP applications
-- table alias  : authm	

-- schemas section -------------------------------------------------
 
-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
; 

-- table section -------------------------------------------------
create table master.authentication_method (

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
 alter table master.authentication_method add constraint pk_authm_code primary key (code, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_logmeth_<colX> on master.authentication_method (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.authentication_method is 'Table to store list of Authentication methods used across the MOSIP applications'
;

