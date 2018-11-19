-- create table section -------------------------------------------------
-- schema 		: reg	    		- Registration Module
-- table 		: machine_master	- Master List of Machines used for registration
-- table alias  : machm

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;


-- table section -------------------------------------------------
create table reg.machine_master (

	id 			character varying (36) not null,

	name 		character varying (64) not null,
	mac_address character varying (64) not null,
	serial_num 	character varying (64) not null,
	ip_address 	character varying (17) ,           -- ip address, usually dynamic, so optional.
	
	validity_end_dtimes timestamp, 

	mspec_id    character varying(36) not null ,	-- master.machine_spec.id ,  spec mapped to machine_type
	
	lang_code   character varying (3) not null,	-- master.language.code
	
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
 alter table reg.machine_master add constraint pk_machm_id primary key (id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_machm_<colX> on reg.machine_master (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table reg.machine_master is 'Table to store master list of machines like desktop, laptop, notebook...etc used at registration centers for individual registration'
;

