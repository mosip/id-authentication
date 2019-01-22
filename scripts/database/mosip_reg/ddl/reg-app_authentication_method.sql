-- create table section -------------------------------------------------
-- schema 		: reg	    				-- Registration client, referenced from master data 
-- table 		: app_authentication_method	-- List of application, process, role and their user authentication methods with sequence
-- table alias  : appauthm	

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
  
-- table section -------------------------------------------------
create table reg.app_authentication_method (
	
	app_id 			  	character varying (36) not null,  	-- reg.app_detail.id
	process_id 			character varying (36) not null,    -- reg.process_list.id -- login auth, packet auth, exception auth, eod auth, MV authentication..etc
	role_code 			character varying (36) not null,  	-- reg.role_list.code
	auth_method_code 	character varying (36) not null,	-- reg.authentication_method.code
	method_seq 			smallint,
	
	lang_code  		character varying(3) not null ,     	-- master.language.code
	
	is_active 		boolean not null,
	cr_by 			character varying (32) not null,
	cr_dtimes		timestamp not null,
	upd_by  		character varying (32),
	upd_dtimes 		timestamp,
	is_deleted 		boolean,
	del_dtimes 		timestamp

)
;

-- keys section -------------------------------------------------
 alter table reg.app_authentication_method add constraint pk_appauthm_id primary key (app_id, process_id, role_code, auth_method_code)
 ;

 -- indexes section -------------------------------------------------
create unique index uk_appauthm_id on reg.app_authentication_method (app_id, process_id, role_code, method_seq) 
;

-- indexes section -------------------------------------------------
-- create index idx_appauthm_<colX> on reg.app_authentication_method (colX )
-- ;

-- comments section ------------------------------------------------- 
-- comment on table reg.app_authentication_method is 'Table to store all List of application, process, role and their user authentication methods with sequence'
-- ;

