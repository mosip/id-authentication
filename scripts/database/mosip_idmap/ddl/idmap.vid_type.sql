-- create table section ----------------------------------------------------------------
-- schema 		: idmap			  - idmap schema
-- table 		: vid_type - vid types
-- table alias  : vidtyp

-- schemas section -------------------------------------------------------------

-- create schema if  idmap reference schema not exists
create schema if not exists idmap
;

-- table section ---------------------------------------------------------------
create table idmap.vid_type (
	code character varying(36) not null,   
	descr character varying(256),
	no_of_instances smallint,
	validity_duration smallint,
	no_of_transactions_allowed smallint,
	auto_replenish boolean,
	lang_code character varying(3) not null,   -- idmap.language.code
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp
	
)
;

-- keys section ------------------------------------------------------------------
alter table idmap.vid_type add constraint pk_vidtyp primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_authtyp_<colX> on idmap.vid_type(colX )
-- ;

-- comments section -------------------------------------------------------------
comment on table idmap.vid_type is 'Master list of VID Type table, stores valid list of VID types and its policy that will be used to manage the vids'
;
comment on column idmap.vid_type.code is 'Primary key of vid type, with lang_cd for multi language '
;
