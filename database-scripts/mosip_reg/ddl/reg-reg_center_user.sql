-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration schema
-- table 		: reg_center_user	- MOSIP Application users and registration center mapping
-- table alias  : cntrusr

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------
create table reg.reg_center_user (

	regcntr_id character varying (36) not null,	 -- reg.registration_center.id
	usr_id  character varying (36) not null,	 -- reg.user_detail.id

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp
)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_user add constraint pk_cntrusr_usr_id primary key (regcntr_id, usr_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrusr_<colX> on reg.reg_center_user (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table reg.reg_center_user is 'Table to store all MOSIP Application users and their mapped registration centers'
;

