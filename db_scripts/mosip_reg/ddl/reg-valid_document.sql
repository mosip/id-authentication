-- create table section --------------------------------------------------------
-- schema 		: reg  - registration schema
-- table 		: valid_document  - reg valid_document list, mapping table for document type and category. 
-- table alias  : valdoc	

-- table section -------------------------------------------------------------------------------

	create table reg.valid_document
	(  
		doctyp_code character varying(36) not null,   	-- reg.doc_type.code 
		doccat_code character varying(36) not null,		-- reg.doc_cateogory.code
			
		lang_code  	character varying(3) not null ,		-- reg.language.code
						
		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes 	timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.valid_document add constraint pk_valdoc_code primary key (doctyp_code, doccat_code)
 ;
