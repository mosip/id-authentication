-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: valid_document  - reg valid_document list, mapping table for document type and category. 
-- table alias  : valdoc	

-- schemas section ---------------------------------------------------------------

-- create schema if reg  schema not exists
create schema if not exists reg
;

-- table section -------------------------------------------------------------------------------

	create table reg.valid_document
	(  
		doctyp_code character varying(36) not null,   	-- reg.doc_type.code 
		doccat_code character varying(36) not null,		-- reg.doc_cateogory.code
			
		lang_code  	character varying(3) not null ,		-- reg.language.code
						
		is_active 	boolean not null,
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes 	timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.valid_document add constraint pk_valdoc_code primary key (doctyp_code, doccat_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_valdoc_<col> on reg.valid_document (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
-- comment on table reg.valid_document is 'reg valid_document table'
-- ;

