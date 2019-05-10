-- create table section --------------------------------------------------------
-- schema 		: reg  		  				- Registration schema
-- table 		: applicant_valid_document  - Registration Applicant valid document list, mapping table for applicant type, document type and category. 
-- table alias  : avaldoc	


-- table section -------------------------------------------------------------------------------

	create table reg.applicant_valid_document (  
		apptyp_code character varying(36) not null,
		doccat_code character varying(36) not null,		-- reg.doc_cateogory.code
		doctyp_code character varying(36) not null,   	-- reg.doc_type.code 

			
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
alter table reg.applicant_valid_document add constraint pk_avaldoc_code primary key (apptyp_code, doccat_code, doctyp_code)
 ;
