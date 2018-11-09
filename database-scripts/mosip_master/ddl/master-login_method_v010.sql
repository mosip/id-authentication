-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: login_method		- List of login methods used across the MOSIP applications
-- table alias  : logmeth	

-- schemas section -------------------------------------------------
 
-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
; 

-- table section -------------------------------------------------
create table master.login_method (

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
 alter table master.login_method add constraint pk_logmeth_login_method primary key (code, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_logmeth_<colX> on master.login_method (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.login_method is 'table to store list of login methods used across the MOSIP applications'
;

