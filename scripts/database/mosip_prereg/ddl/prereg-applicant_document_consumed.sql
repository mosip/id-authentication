-- create table section -------------------------------------------------
-- schema 		: prereg							- Pre Registration
-- table 		: applicant_document_consumed		- Pre Registration applicant document submitted details which are consumed
-- table alias  : appldocc			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;
 
-- table section -------------------------------------------------
	
	
	create table prereg.applicant_document_consumed (
		id 				character varying(36) not null,
		prereg_id 		character varying(36) not null, 	-- prereg.applicant_demographic.prereg_id
	
		doc_name		character varying(128) not null ,
		doc_cat_code 	character varying(36) not null ,	-- master.doc_category.code
		doc_typ_code	character varying(36) not null ,	-- master.doc_type.code
		doc_file_format	character varying(36) not null ,	-- master.doc_format.code
		doc_id 			character varying(128) not null,
		doc_hash 		character varying(64) not null ,
			
		encrypted_dtimes timestamp not null ,
		
		status_code 	character varying(36) not null ,	-- prereg.status_list.code
		lang_code  		character varying(3) not null ,     -- master.language.code
							
		cr_by 		character varying (256) ,
		cr_dtimes	timestamp ,
		upd_by  	character varying (256) ,
		upd_dtimes timestamp
		
	)
;

-- keys section -------------------------------------------------
alter table prereg.applicant_document_consumed add constraint pk_appldocc_prereg_id primary key (id)
;

-- indexes section -------------------------------------------------
create unique index idx_appldocc_prereg_id on prereg.applicant_document_consumed (prereg_id, doc_cat_code, doc_typ_code) 
;

-- comments section ------------------------------------------------- 
comment on table prereg.applicant_document_consumed is 'applicant_document_consumed table stores applicant demographic document proof submitted details which are consumed'
;

