	
-- create table section -----------------------------------------------------------------------------------------------------------
-- schema 		: master		- Master Reference schema
-- table 		: template      - Templates to store all templates files used across the mosip modules
-- table alias  : tmplt	
  
-- schemas section -----------------------------------------------------------------------------------------------------------------

-- create schema if master schema for Master reference tables is not exists
create schema if not exists master
;
 
-- table section --------------------------------------------------------------------------------------------------------------------
create table master.template (

	id 					character varying (36) not null ,

	name				character varying (128) not null ,
	descr 				character varying (256) ,
	file_format_code 	character varying (36) not null,		-- master.template_file_format.code
	model 				character varying (128) ,
	file_txt 			character varying (4086),
	
	module_id       	character varying (36)  ,				-- master.module_detail.id
	module_name     	character varying (128) ,
	
	template_typ_code	character varying (36) not null ,		-- master.template_type.code
	lang_code 			character varying (3) not null ,		-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null ,
	upd_by  	character varying (256),
	upd_dtimes 	timestamp,
	is_deleted 	boolean,
	del_dtimes	timestamp
		
)
;

-- keys section -----------------------------------------------------------------------------------------------------------------------
alter table master.template add constraint pk_tmplt_id primary key (id, lang_code)
 ;

-- indexes section --------------------------------------------------------------------------------------------------------------------
-- create index idx_tmplt_<colx> on master.template (<colx>)
-- ;

