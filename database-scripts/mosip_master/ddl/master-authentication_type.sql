-- create table section ----------------------------------------------------------------
-- schema 		: master			  - Master schema
-- table 		: authentication_type - Master Reference Authentication Type
-- table alias  : authtyp

-- schemas section -------------------------------------------------------------

-- create schema if  master reference schema not exists
create schema if not exists master
;

-- table section ---------------------------------------------------------------
create table master.authentication_type (

	code character varying(36) not null,   
	descr character varying(256),

	lang_code character varying(3) not null,   -- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section ------------------------------------------------------------------
alter table master.authentication_type add constraint pk_authtyp_code primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_authtyp_<colX> on master.authentication_type(colX )
-- ;

-- comments section -------------------------------------------------------------
comment on table master.authentication_type is 'Master Authentication Type table, stores authentication types like Demo Authentication, Bio Authentication, OTP Authentication...etc'
;
comment on column master.authentication_type.code is 'Primary key of authentication code, with lang_cd for multi language '
;
