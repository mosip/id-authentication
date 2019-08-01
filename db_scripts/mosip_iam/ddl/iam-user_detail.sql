-- create table section -------------------------------------------------
-- schema 		: iam	    	- Identity and Access Management Madule
-- table 		: user_detail	- Application users details
-- table alias  : usrdtl

-- schemas section -------------------------------------------------

-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;

-- table section -------------------------------------------------
create table iam.user_detail (
	
	id 			character varying (256) not null,
	reg_id		character varying (39),					-- RID of indivisuals and referenced from idrepo database 
	name 		character varying (64) not null,
	email 		character varying (256),
	mobile 		character varying (16),
	salt 		character varying (64),
	
	status_code character varying(36) not null,			-- master.status_list.code
	lang_code 	character varying (3) not null,			-- master.language.code
		
	last_login_dtimes timestamp,
	last_login_method character varying (64),			-- master.login_method.code

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
 alter table iam.user_detail add constraint pk_usrdtl_id primary key (id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_usrdtl_<ColX> on iam.user_detail (<ColX>)
-- ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE iam.user_detail IS 'User Detail : List of applicatgion users in the system, who can perform UIN registration functions as per roles assigned.' ;

COMMENT ON COLUMN iam.user_detail.id IS 'User ID : Unique ID generated / assigned for a user' ;
COMMENT ON COLUMN iam.user_detail.reg_id IS 'Registration ID: RID of the user. Typically this will be used for authentication and validation of users' ;
COMMENT ON COLUMN iam.user_detail.name IS 'Name : User name' ;
COMMENT ON COLUMN iam.user_detail.email IS 'Email: Email address of the user' ;
COMMENT ON COLUMN iam.user_detail.mobile IS 'Mobile: Mobile number of the user' ;
COMMENT ON COLUMN iam.user_detail.status_code IS 'Status Code: User status. Refers to master.status_iam.code' ;
COMMENT ON COLUMN iam.user_detail.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN iam.user_detail.last_login_dtimes IS 'Last Login Datetime: Date and time of the last login by the user' ;
COMMENT ON COLUMN iam.user_detail.last_login_method IS 'Last Login Method: Previous login method in which the user logged into the system' ;

COMMENT ON COLUMN iam.user_detail.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.user_detail.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.user_detail.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.user_detail.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.user_detail.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.user_detail.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.user_detail.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;

