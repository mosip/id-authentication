-- create table section -------------------------------------------------
-- schema 		: regprc					- Registration Processor
-- table 		: applicant_document		- Registration processor applicant document details
-- table alias  : appldoc

-- schemas section -------------------------------------------------

-- create schema if regprc schema for Registration Processor schema not exists
create schema if not exists regprc
;

-- table section -------------------------------------------------
	
	create table regprc.applicant_document (
	
		reg_id 			character varying(39) not null ,		-- regprc.registration.id
		prereg_id  		character varying(64) ,					-- no fk.  data populate.
		
		doc_name		character varying(128) not null ,		-- no fk.  data populate.
		doc_cat_code 	character varying(36) not null ,		-- no fk.  data populate.
		doc_typ_code	character varying(36) not null ,		-- no fk.  data populate.
		doc_file_format	character varying(36) not null ,		-- no fk.  data populate.
		doc_owner 		character varying(64),
		doc_store		bytea ,
		
		is_active 	boolean not null,		
		cr_by 		character varying (32) ,
		cr_dtimes	timestamp ,
		upd_by  	character varying (32) ,
		upd_dtimes timestamp,
		is_deleted 	boolean ,
		del_dtimes	timestamp
		
	)
;

-- keys section -------------------------------------------------
alter table regprc.applicant_document add constraint pk_appldoc_reg_id primary key (reg_id, doc_cat_code, doc_typ_code)
;

-- indexes section -------------------------------------------------
--  create index idx_appldoc_reg_id on regprc.applicant_document (reg_id)
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.applicant_document is 'applicant_document table stores applicant demographic document proof submitted details'
;

