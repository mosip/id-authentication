-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration Module
-- table 		: app_login_method	- Registration User Login Method
-- table alias  : applogm		

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
  
-- table section -------------------------------------------------
create table reg.app_login_method (
	
	app_id character varying (36) not null,  			-- master.app_detail.id
	
	login_method_code character varying (36) not null,	-- master.login_method.code
	
	lang_code   character varying (3) not null, 	    -- master.language.code 
	
	method_seq 	smallint,
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes	timestamp  
	
)
;

-- keys section -------------------------------------------------
 alter table reg.app_login_method add constraint pk_applogm_app_id primary key (app_id, login_method_code, lang_code)
 ;


-- indexes section -------------------------------------------------
-- create index idx_usrlm_<colX> on reg.app_login_method (colX )
-- ;

-- comments section ------------------------------------------------- 
 comment on table reg.app_login_method is ' app_login_method table ... '
 ;
