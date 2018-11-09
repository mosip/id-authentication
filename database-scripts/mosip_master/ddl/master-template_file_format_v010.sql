-- create table section --------------------------------------------------------
-- schema 		: master	- Master Reference schema
-- table 		: template_file_format	- Master template file format
-- table alias  : tffmt
 
-- schemas section -------------------------------------------------------------

-- create schema if Master reference schema not exists
create schema if not exists master
;
 
-- table section ---------------------------------------------------------------
create table master.template_file_format (

	code character varying(36) not null,
	
	descr character varying(256) not null,
	lang_code character varying(3) not null,		-- master.language.code
	
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
alter table master.template_file_format add constraint pk_tffmt_code primary key (code, lang_code)
 ;
-- 

-- indexes section --------------------------------------------------------------
-- create index idx_tffmt_<colX> on master.template_file_format (colX )
-- ;

-- comments section -------------------------------------------------------------
comment on table master.template_file_format is 'Master table to store template file formats'
;
comment on column master.template_file_format.code is 'File format '
;

