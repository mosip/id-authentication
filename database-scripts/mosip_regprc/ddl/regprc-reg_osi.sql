-- create table section -----------------------------------------------------------
-- schema 		: regprc			- Registration Processor
-- table 		: reg_osi			- Operator, Supervisor and Introducers for the Registration
-- table alias  : regosi

-- schemas section -----------------------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------------------
create table regprc.reg_osi (

	reg_id 			character varying(39) not null,				--  regprc.registration.id
	prereg_id  		character varying(64) ,						--  no fk.  data populate

	officer_id 					character varying(36) not null,	--  no fk.  data populate
	officer_fingerp_image_name 	character varying(128),
	officer_iris_image_name 	character varying(128),
	officer_fingerp_typ 		character varying(64),
	officer_iris_typ 			character varying(64),
	officer_photo_name 			character varying(128),
	
	officer_hashed_pwd 			character varying (512),
	officer_hashed_pin 			character varying (36),

	supervisor_id 					character varying(36) not null,	--  no fk. data populate.
	supervisor_fingerp_image_name 	character varying(128),
	supervisor_iris_image_name 		character varying(128),
	supervisor_fingerp_typ 			character varying(64),
	supervisor_iris_typ 			character varying(64),
	supervisor_photo_name 			character varying(128),
	
	supervisor_hashed_pwd 			character varying (512),
	supervisor_hashed_pin 			character varying (36),

	introducer_id 		character varying(36),		--  no fk.  data populate.
	introducer_typ 		character varying(64),		--  no fk.  data populate.
	introducer_reg_id 	character varying(39),		--  no fk.  data populate.
	introducer_uin 		character varying(28),		--  no fk.  data populate.
	introducer_fingerp_image_name 	character varying(128),
	introducer_iris_image_name 		character varying(128),
	introducer_fingerp_typ 			character varying(64),
	introducer_iris_typ 			character varying(64),
	introducer_photo_name 			character varying(128),
	
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
alter table regprc.reg_osi add constraint pk_regosi_reg_id primary key (reg_id)
;
 
-- indexes section ------------------------------------------------------------------
-- create index idx_regosi_<colX> on regprc.reg_osi(<colX>)
-- ;

-- comments section -----------------------------------------------------------------
comment on table regprc.reg_osi is 'This table is used to store Operator, Supervisor and Introducers id while processing registration'
;

