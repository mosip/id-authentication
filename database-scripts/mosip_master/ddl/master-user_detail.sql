-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: user_detail	- Application users details
-- table alias  : usrdtl

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.user_detail (
	
	id 			character varying (36) not null,
	
	uin_ref_id 	character varying (28),		-- uin.uin.uin_ref_id 
	
	name 		character varying (64) not null,
	
	email 		character varying (64),
	mobile 		character varying (16),
	
	status_code character varying(36) not null,			-- master.status_list.code
	
	lang_code 	character varying (3) not null,			-- master.language.code
		
	last_login_dtimes timestamp,
	last_login_method character varying (64),			-- master.login_method.code

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
 alter table master.user_detail add constraint pk_usrdtl_id primary key (id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrdtl_uin_ref_id on master.user_detail (uin_ref_id)
-- ;

-- comments section ------------------------------------------------- 
comment on table master.user_detail is 'Application user details, List of user will have access to MOSIP applications based on assigned roles'
;

