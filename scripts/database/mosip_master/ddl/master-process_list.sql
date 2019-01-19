-- create table section -------------------------------------------------
-- schema 		: master	    	- Master reference Module
-- table 		: process_list		- MOSIP Application process master list
-- table alias  : prclst	
 
-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.process_list (
	
	id 		character varying (36) not null,   
	
	name 	character varying (64) not null,
	descr 	character varying (256),
	
	lang_code 	character varying (3) not null,   -- master.language.code 
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table master.process_list add constraint pk_prclst_id primary key (id, lang_code)
 ;


-- comments section ------------------------------------------------- 
comment on table master.process_list is 'MOSIP Application process master list'
;

