-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: doc_type  - Master doc_type list
-- table alias  : doctyp	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.doc_type (
		
		code  character varying(36) not null ,       -- PASSPORT, VOTERID, etc.
		
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	 -- master.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes  timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 

	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.doc_type add constraint pk_doctyp_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_doctyp_<col> on master.doc_type (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.doc_type is 'Master doc_type table'
;

