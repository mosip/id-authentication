-- create table section -------------------------------------------------
-- schema 		: iam	    	- Identity and Access Management Madule
-- table 		: user_pwd		- Application users and Password
-- table alias  : usrpwd	
 
-- schemas section -------------------------------------------------

-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;
 
-- table section -------------------------------------------------
create table iam.user_pwd (

	usr_id 				character varying (256) not null,			-- iam.user_detail.id
	pwd 				character varying (512) not null,
	pwd_expiry_dtimes 	timestamp,

	status_code 		character varying(64) not null,		-- master.status_list.code
	lang_code 			character varying(3) not null,		-- master.language.code
	
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
 alter table iam.user_pwd add constraint pk_usrpwd_usr_id primary key (usr_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrpwd_<colX> on iam.user_pwd (colX )
-- ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE iam.user_pwd IS 'User Password : Stores encripted password of users in iam.user_details table. ' ;

COMMENT ON COLUMN iam.user_pwd.usr_id IS 'User ID : registration center id refers to iam.user_detail.id' ;
COMMENT ON COLUMN iam.user_pwd.pwd IS 'Password: User password in encrypted form' ;
COMMENT ON COLUMN iam.user_pwd.pwd_expiry_dtimes IS 'Password Expiry Datetime: User password expiry date and time based on password policy' ;
COMMENT ON COLUMN iam.user_pwd.status_code IS 'Status Code: User password status. Refers to master.status_list.code' ;
COMMENT ON COLUMN iam.user_pwd.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;

COMMENT ON COLUMN iam.user_pwd.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.user_pwd.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.user_pwd.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.user_pwd.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.user_pwd.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.user_pwd.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.user_pwd.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;