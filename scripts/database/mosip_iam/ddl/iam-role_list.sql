-- create table section -------------------------------------------------
-- schema 		: iam	    	- Identity and Access Management Madule
-- table 		: role_list	 	- List of roles used across MOSIP Modules
-- table alias  : rolelst		

-- schemas section -------------------------------------------------
 
-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;

-- table section -------------------------------------------------
create table iam.role_list (
	
	code 		character varying (36) not null,
	descr 		character varying (256),
	
	lang_code 	character varying (3) not null,		-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table iam.role_list add constraint pk_rolelst_code primary key (code, lang_code)
 ;

-- indexes section -------------------------------------------------
-- create index idx_rolelst_<colX> on iam.role_list (colX )
-- ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE iam.role_list IS 'Role List : List of roles defined within the system. These roles are used for various processes like data access, authentication methods, authorization, etc.' ;

COMMENT ON COLUMN iam.role_list.code IS 'Code : list of roles defined for security , authorization,  permission for various modules/processes, etc.' ;
COMMENT ON COLUMN iam.role_list.descr IS 'Description : Role description' ;
COMMENT ON COLUMN iam.role_list.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;

COMMENT ON COLUMN iam.role_list.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.role_list.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.role_list.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.role_list.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.role_list.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.role_list.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.role_list.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;