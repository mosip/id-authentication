-- create table section -------------------------------------------------
-- schema 		: reg	    					-- Registration Module
-- table 		: app_authentication_method		-- Registration User Authentication Methods
-- table alias  : appauthm		

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
  
-- table section -------------------------------------------------
create table reg.app_authentication_method (
	
	app_id 				character varying (36) not null,  	-- master.app_detail.id
	auth_method_code 	character varying (36) not null,	-- master.authentication_method.code
	process_name 		character varying (64) not null,    -- login authentication, packet authentication, exception authentication, eod authentication, MV authentication..etc
	
	lang_code   character varying (3) not null, 	    	-- master.language.code 
	
	method_seq 	smallint,
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes	timestamp  
	
)
;

-- keys section -------------------------------------------------
 alter table reg.app_authentication_method add constraint pk_appauthm_id primary key (app_id, auth_method_code, process_name, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_usrlm_<colX> on reg.app_authentication_method (colX )
-- ;

-- comments section ------------------------------------------------- 
-- comment on table reg.app_authentication_method is ' Application, Application process Authentication methods '
-- ;
