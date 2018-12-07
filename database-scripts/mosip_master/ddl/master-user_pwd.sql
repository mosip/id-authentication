-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: user_pwd	- Application users and Password
-- table alias  : usrpwd	
 
-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------
create table master.user_pwd (

	usr_id 		character varying (36) not null,			-- master.user_detail.id

	pwd 				character varying (512) not null,
	pwd_expiry_dtimes 	timestamp,

	status_code 		character varying(64) not null,		-- master.status_list.code
	lang_code 			character varying(3) not null,		-- master.language.code
	
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
 alter table master.user_pwd add constraint pk_usrpwd_usr_id primary key (usr_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrpwd_<colX> on master.user_pwd (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.user_pwd is 'Table used to store all MOSIP Applications users and password'
;
