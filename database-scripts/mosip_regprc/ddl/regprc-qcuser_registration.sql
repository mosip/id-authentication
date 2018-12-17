-- create table section ---------------------------------------------------
-- schema 		: regprc			  - Registration Processor (enrolment server or ID issuance server)
-- table 		: qcuser_registration - Mapping / Assigning table of registration to QC user
-- table alias  : qcureg

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;
 
-- table section ---------------------------------------------------------
create table regprc.qcuser_registration (

	reg_id  character varying(39) not null,				-- regprc.registration.id
	usr_id  character varying (36) not null,			-- master.user_detail.id

	status_code    character varying(36),				-- master.status_list.code
	status_comment character varying(256),

	lang_code character varying(3) not null,			-- master.language.code
	
	is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table regprc.qcuser_registration add constraint pk_qcureg_usr_id primary key (reg_id,usr_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_qcureg_<colX> on regprc.qcuser_registration (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.qcuser_registration is 'Mapping / Assigning table of registration to QC user'
;

