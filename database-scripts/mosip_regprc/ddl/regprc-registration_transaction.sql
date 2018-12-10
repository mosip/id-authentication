-- create table section ---------------------------------------------------
-- schema 		: regprc				- Registration Processor (enrolment server or ID issuance server)
-- table 		: registration_transaction	- Registration Processor / Enrolment Packet and Transactions.
-- table alias  : regtrn

-- schemas section -------------------------------------------------------

-- create schema if Registration Processor schema not exists
create schema if not exists regprc
;

-- table section ----------------------------------------------------------
create table regprc.registration_transaction (

	id character varying(36) not null,

	reg_id character varying(39) not null,			-- regprc.registration.id

	trn_type_code character varying(64) not null,	-- regprc.transaction_type.code
	remarks character varying(256), 
	
	parent_regtrn_id character varying(36),			-- optional,  self join.
	
	ref_id        character varying(64) ,			
	ref_id_type   character varying(64) ,			-- master.id_type.code
	
	status_code character varying(36) not null,		-- regproc.status_list.code
	lang_code character varying(3) not null,		-- master.language.code
	status_comment character varying(256),

	-- is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section ----------------------------------------------------------------
 alter table regprc.registration_transaction add constraint pk_regtrn_id primary key (id)
 ;
-- 

-- indexes section ----------------------------------------------------------------
-- create index idx_regtrn_<colX> on regprc.registration_transaction (colX )
-- ;

-- comments section --------------------------------------------------------------
comment on table regprc.registration_transaction is 'Registration Processor Transaction table is to store ALL  Registration Processor packet processing/process transaction details for ID issuance'
;
