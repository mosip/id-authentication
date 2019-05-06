-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: doc_type  - reg doc_type list
-- table alias  : doctyp	

-- table section -------------------------------------------------------------------------------

	create table reg.doc_type (
		
		code  character varying(36) not null ,       -- PASSPORT, VOTERID, etc.
		
		name  character varying (64) not null ,
		descr  character varying (128) ,
		
		lang_code  character varying(3) not null ,	 -- reg.language.code
	
		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes  timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 

	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.doc_type add constraint pk_doctyp_code primary key (code, lang_code)
;

