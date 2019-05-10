-- create table section -------------------------------------------------
-- schema 		: reg	    - Registration Module
-- table 		: user_pwd	- Registration user Roles
-- table alias  : usrpwd	

-- table section -------------------------------------------------
create table reg.user_pwd (

	usr_id 		character varying (256) not null,			-- reg.user_detail.id

	pwd 				character varying (512) not null,
	pwd_expiry_dtimes 	timestamp,

	status_code 		character varying(64) not null,		-- reg.status_list.code
	lang_code 			character varying(3) not null,		-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp

)
;

-- keys section -------------------------------------------------
 alter table reg.user_pwd add constraint pk_usrpwd_usr_id primary key (usr_id)
 ;
