-- create table section ---------------------------------------------------
-- schema 		: regprc	- Registration Processor (enrolment server or ID issuance server)
-- table 		: registration - Registration Processor 
-- table alias  : reg

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------
create table regprc.registration (

	id character varying(39) not null,
	
	reg_type character varying(36) not null, 		-- master.appl_form_type.code
	ref_reg_id character varying(39),				-- optional, id for reference, not foreign key.
	applicant_type  character varying(36),

	status_code character varying(36) not null,		-- regprc.status_list.code
	
	lang_code character varying(3) not null,		-- master.language.code
	status_comment character varying(256),

	latest_trn_id character varying(36),			-- regprc.registration_transaction.id
	
	trn_retry_count smallint,
	
	pkt_cr_dtimes timestamp,

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
 alter table regprc.registration add constraint pk_reg_id primary key (id)
 ;
-- 

-- indexes section -------------------------------------------------
-- create index idx_reg_<colX> on regprc.registration (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.registration is 'Registration Processor table is to store id issuance enrolment id and packet id details'
;

--comment on column regprc.registration.<columnname> is 'comment on a column'
--;
