-- create table section ---------------------------------------------------
-- schema 		: regprc		- Registration Processor (enrolment server or ID issuance server)
-- table 		: reg_uin 	- Registration id and uin mapping table
-- table alias  : reguin

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------
create table regprc.reg_uin (

	reg_id 	character varying(39) not null, -- regprc.registration.id
	uin 	character varying(28) not null, -- UIN of the resident

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
 alter table regprc.reg_uin add constraint pk_reguin_id primary key (reg_id)
 ;
-- 

-- indexes section -------------------------------------------------
-- create index idx_reguin_<colX> on regprc.reg_uin (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.reg_uin is 'reg_uin table is to store registration id and uin mappings'
;

--comment on column regprc.reg_uin.<columnname> is 'comment on a column'
--;
