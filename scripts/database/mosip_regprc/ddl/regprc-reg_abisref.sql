-- create table section ---------------------------------------------------
-- schema 		: regprc				- Registration Processor (enrolment server or ID issuance server)
-- table 		: reg_abisref_tbr 		- Registration ID and ABIS Reference ID mapping table 
-- table alias  : regref

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------
create table regprc.reg_abisref_tbr_tbr (

	reg_id 		character varying(39) not null,  	-- regprc.registration.id
	abis_ref_id character varying(36) not null,
	
	is_active boolean not null,
	cr_by character varying (256) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
	
)
;

-- keys section ----------------------------------------------------
 alter table regprc.reg_abisref_tbr add constraint pk_regref_id primary key (reg_id)
 ;
-- 

-- indexes section -------------------------------------------------
-- create index idx_regref_<colX> on regprc.reg_abisref_tbr (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.reg_abisref_tbr is 'reg_abisref_tbr table is to store registration id and ABIS reference id mapping'
;

--comment on column regprc.reg_abisref_tbr.<columnname> is 'comment on a column'
--;
