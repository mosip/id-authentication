	
-- create table section -----------------------------------------------------------------------------------------------------------
-- schema 		: reg		- registration schema
-- table 		: template      - Templates to store all templates files used across the mosip modules
-- table alias  : tmplt	

-- table section --------------------------------------------------------------------------------------------------------------------
create table reg.template (

	id 					character varying (36) not null ,

	name				character varying (128) not null ,
	descr 				character varying (256) ,
	file_format_code 	character varying (36) not null,		-- master.template_file_format.code
	model 				character varying (128) ,
	file_txt 			character varying (4086),
	
	module_id       	character varying (36)  ,				-- master.module_detail.id
	module_name     	character varying (128) ,
	
	template_typ_code	character varying (36) not null ,		-- reg.template_type.code
	lang_code 			character varying (3) not null ,		-- reg.language.code
	
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
alter table reg.template add constraint pk_tmplt_id primary key (id, lang_code)
 ;

