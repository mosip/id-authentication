-- create table section --------------------------------------------------------
-- schema 		: regprc	- regprc schema
-- table 		: transaction_type	- regprc Transaction Type
-- table alias  : trntyp	

-- schemas section -------------------------------------------------------------

-- create schema if regprc schema not exists
create schema if not exists regprc
;

-- table section ---------------------------------------------------------------
create table regprc.transaction_type (

	code character varying(36) not null,   
	descr character varying(256) not null,

	lang_code character varying(3) not null, 	-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
)
;

-- keys section ------------------------------------------------------------------
alter table regprc.transaction_type add constraint pk_trntyp_code primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_trntyp_<colX> on regprc.transaction_type (colX )
-- ;

-- comments section -------------------------------------------------------------
comment on table regprc.transaction_type is 'Registration Process Transaction Type table, '
;
comment on column regprc.transaction_type.code is 'Primary key of transaction code, with lang_cd for multi language '
;
