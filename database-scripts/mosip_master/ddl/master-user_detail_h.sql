-- create table section -------------------------------------------------
-- schema 		: master	    - Master reference Module
-- table 		: user_detail_h	- HISTORY :  Application users details
-- table alias  : usrdtl_h

-- schema section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.user_detail_h (
	
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
	del_dtimes  timestamp,
	
	eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.

)
;

-- keys section -------------------------------------------------
 alter table master.user_detail_h add constraint pk_usrdtl_h_id primary key (id, eff_dtimes)
 ;
 
-- indexes section -------------------------------------------------
-- create index idx_usrdtl_h_uin_ref_id on master.user_detail_h (uin_ref_id)
-- ;

-- comments section ------------------------------------------------- 
comment on table master.user_detail_h is 'History table : Application user details, List of user will have access to MOSIP applications based on assigned roles'
;

