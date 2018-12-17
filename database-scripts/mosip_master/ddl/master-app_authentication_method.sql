-- create table section -------------------------------------------------
-- schema 		: master	    			-- Master reference Module
-- table 		: app_authentication_method	-- List of application and their user authentication methods
-- table alias  : appauthm	

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;
  
-- table section -------------------------------------------------
create table master.app_authentication_method (
	
	app_id 			  	character varying (36) not null,  	-- master.app_detail.id
	
	auth_method_code 	character varying (36) not null,	-- master.authentication_method.code
	process_name 		character varying (64) not null,    -- login aithentication, packet authentication, exception authentication, eod authentication, MV authentication..etc
	
	method_seq 		smallint,
	
	lang_code 		character varying (3) not null,			-- master.language.code 
	
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
 alter table master.app_authentication_method add constraint pk_appauthm_id primary key (app_id, auth_method_code, process_name, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_appauthm_<colX> on master.app_authentication_method (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.app_authentication_method is 'Table to store all MOSIP Application, Application process and their user authentication methods'
;

