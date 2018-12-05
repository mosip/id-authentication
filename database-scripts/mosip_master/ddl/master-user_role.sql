-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: user_role	- MOSIP Application users and Role mapping
-- table alias  : usrrol		

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------
create table master.user_role (

	usr_id 		character varying (36) not null,	-- master.user_detail.id

	role_code 	character varying (36) not null,  	-- master.role_list.code

	lang_code 	character varying (3) not null,		-- master.language.code
	
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
 alter table master.user_role add constraint pk_usrrol_usr_id primary key (usr_id, role_code)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrrol_<colX> on master.user_role (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.user_role is 'Table to store all MOSIP Application users and their mapped roles'
;

