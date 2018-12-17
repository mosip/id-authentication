-- create table section -------------------------------------------------
-- schema 		: master	    	- Master reference Module
-- table 		: machine_master_h	- HISTORY : Master List of Machines used for registration 
-- table alias  : machm_h
 
-- schema section -------------------------------------------------
 
-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------
create table master.machine_master_h (

	id 			character varying (36) not null,

	name 		character varying (64) not null,
	mac_address character varying (64) not null,
	serial_num 	character varying (64) not null,
	ip_address 	character varying (17) ,           -- ip address, usually dynamic, so optional.
	
	validity_end_dtimes timestamp,

	mspec_id    character varying(36) not null ,   -- master.machine_spec.id ,  spec mapped to machine_type
	
	lang_code   character varying (3) not null,	   -- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp,
	
	eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.

)
;

-- keys section -------------------------------------------------
 alter table master.machine_master_h add constraint pk_machm_h_id primary key (id, eff_dtimes)
 ;

-- indexes section -------------------------------------------------
-- create index idx_machm_h_<colX> on master.machine_master_h (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table master.machine_master_h is 'History table :  Table to store master list of machines like desktop, laptop, notebook...etc used at registration centers for individual registration'
;

