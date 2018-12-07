-- create table section ---------------------------------------------------
-- schema 		: regprc				  - Registration Processor (enrolment server or ID issuance server)
-- table 		: reg_manual_verification - Manual Verification and Registration Mapping / Assignment table of registration to manual verifier
-- table alias  : rmnlver

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;
 
-- table section ---------------------------------------------------------
create table regprc.reg_manual_verification (
	
	reg_id				character varying(39) not null, 	-- regprc.registration.id
	matched_ref_id		character varying(39) not null,
	matched_ref_type	character varying(36) not null,
	mv_usr_id			character varying (36), 			-- master.user_detail.id
	matched_score      	numeric(6,3) ,
	status_code    	character varying(36),		-- master.status_list.code
	reason_code    	character varying(36),		-- master.reason_list.code
	status_comment 	character varying(256),			
	
	lang_code 		character varying(3) not null,	-- master.language.code
	
	is_active	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table regprc.reg_manual_verification add constraint pk_rmnlver_id primary key (reg_id,matched_ref_id,matched_ref_type)
 ;

-- indexes section -------------------------------------------------
-- create index idx_rmnlver_<colX> on regprc.reg_manual_verification (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.reg_manual_verification is 'Manual Verification and Registration Mapping / Assignment table of registration to MA user'
;

