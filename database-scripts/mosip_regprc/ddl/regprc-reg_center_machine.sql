-- create table section -----------------------------------------------------------
-- schema 		: regprc					- Registration Processor
-- table 		: reg_center_machine		- Registration center, Machine details for a individual registration
-- table alias  : rcntrm

-- schemas section -----------------------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------------------
create table regprc.reg_center_machine (

	reg_id 			character varying(39) not null,		-- regprc.registration.id

	prereg_id  		character varying(64) ,				-- no fk.  only populate.
	
	machine_id 			character varying (36) not null,	-- no fk.  only populate.
	regcntr_id 			character varying (36) not null,	-- no fk.  only populate.
	packet_cr_dtimes 	timestamp ,

	latitude  		character varying(32) ,
	longitude 		character varying(32) ,
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section --------------------------------------------------------------------
alter table regprc.reg_center_machine add constraint pk_rcntrm_reg_id primary key (reg_id)
;
 
-- indexes section ------------------------------------------------------------------
-- create index idx_rosi_<colX> on regprc.reg_center_machine(<colX>)
-- ;

-- comments section -----------------------------------------------------------------
comment on table regprc.reg_center_machine is 'This table is used to store Registration center, Machine details for a individual registration'
;

