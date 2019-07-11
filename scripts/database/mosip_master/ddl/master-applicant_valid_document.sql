-- create table section --------------------------------------------------------
-- schema 		: master  		  			- Master Reference schema
-- table 		: applicant_valid_document  - Master Applicant valid document list, mapping table for applicant type, document type and category. 
-- table alias  : avaldoc	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table master.applicant_valid_document (  
		apptyp_code character varying(36) not null,
		doccat_code character varying(36) not null,		-- master.doc_cateogory.code
		doctyp_code character varying(36) not null,   	-- master.doc_type.code 
			
		lang_code  	character varying(3) not null ,		-- master.language.code
						
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
alter table master.applicant_valid_document add constraint pk_avaldoc_code primary key (apptyp_code, doccat_code, doctyp_code)
 ;

-- comments section -------------------------------------------------------------------------- 
COMMENT ON TABLE master.applicant_valid_document IS 'Applicant Valid Document : This is mapping table that relates applicant type, document category and document type, that is valid document proof for UIN registration process.' ;

COMMENT ON COLUMN master.applicant_valid_document.apptyp_code IS 'Applicant Type Code: Code of the applicant type defined/configured by the country admin' ;
COMMENT ON COLUMN master.applicant_valid_document.doccat_code IS 'Document Category Code: Refers to master.doc_category.code' ;
COMMENT ON COLUMN master.applicant_valid_document.doctyp_code IS 'Document Type Code: Refers to master.doc_type.code' ;
COMMENT ON COLUMN master.applicant_valid_document.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN master.applicant_valid_document.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN master.applicant_valid_document.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN master.applicant_valid_document.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN master.applicant_valid_document.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN master.applicant_valid_document.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN master.applicant_valid_document.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN master.applicant_valid_document.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;