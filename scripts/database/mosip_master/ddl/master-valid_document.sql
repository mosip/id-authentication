-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: valid_document  - Master valid_document list, mapping table for document type and category. 
-- table alias  : valdoc	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.valid_document
	(  
		doctyp_code character varying(36) not null,   	-- master.doc_type.code 
		doccat_code character varying(36) not null,		-- master.doc_cateogory.code
			
		lang_code  	character varying(3) not null ,		-- master.language.code
						
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
alter table master.valid_document add constraint pk_valdoc_code primary key (doctyp_code, doccat_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_valdoc_<col> on master.valid_document (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.valid_document is 'Master valid_document table'
;

