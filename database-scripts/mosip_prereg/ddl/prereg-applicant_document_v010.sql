-- create table section -------------------------------------------------
-- schema 		: prereg					- Pre Registration
-- table 		: applicant_document		- Pre Registration applicant document submitted details
-- table alias  : appldoc			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;
 
-- table section -------------------------------------------------
	
	
	create table prereg.applicant_document (
		id 				character varying(36) not null,
		prereg_id 		character varying(36) not null, 	-- prereg.applicant_demographic.prereg_id
	
		doc_name		character varying(128) not null ,
		doc_cat_code 	character varying(36) not null ,	-- master.doc_category.code
		doc_typ_code	character varying(36) not null ,	-- master.doc_type.code
		doc_file_format	character varying(36) not null ,	-- master.doc_format.code
		doc_store		bytea ,
			
		status_code 	character varying(36) not null ,	-- prereg.status_list.code
		lang_code  		character varying(3) not null ,     -- master.language.code
							
		cr_by 		character varying (32) ,
		cr_dtimes	timestamp ,
		upd_by  	character varying (32) ,
		upd_dtimes timestamp
		
	)
;

-- keys section -------------------------------------------------
alter table prereg.applicant_document add constraint pk_appldoc_prereg_id primary key (id)
;

-- indexes section -------------------------------------------------
create unique index idx_appldoc_prereg_id on prereg.applicant_document (prereg_id, doc_cat_code, doc_typ_code) 
;

-- comments section ------------------------------------------------- 
comment on table prereg.applicant_document is 'applicant_document table stores applicant demographic document proof submitted details'
;

