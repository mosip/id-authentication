-- create table section -------------------------------------------------
-- schema 		: iam	    	- Identity and Access Management Madule
-- table 		: user_role	- MOSIP Application users and Role mapping
-- table alias  : usrrol		

-- schemas section -------------------------------------------------

-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;
 
-- table section -------------------------------------------------
create table iam.user_role (

	usr_id 		character varying (256) not null,	-- iam.user_detail.id
	role_code 	character varying (36) not null,  	-- iam.role_list.code

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
 alter table iam.user_role add constraint pk_usrrol_usr_id primary key (usr_id, role_code)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrrol_<colX> on iam.user_role (colX )
-- ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE iam.user_role IS 'User Role : List of user roles as per the security and access rights, that will be assigned to specific users.' ;

COMMENT ON COLUMN iam.user_role.usr_id IS 'User ID : registration center id refers to iam.user_detail.id' ;
COMMENT ON COLUMN iam.user_role.role_code IS 'Role Code: Role assigned to the user for access control. Refers to iam.role_list.code' ;
COMMENT ON COLUMN iam.user_role.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;

COMMENT ON COLUMN iam.user_role.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.user_role.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.user_role.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.user_role.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.user_role.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.user_role.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.user_role.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;