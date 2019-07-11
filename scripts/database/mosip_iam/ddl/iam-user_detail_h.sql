-- create table section -------------------------------------------------
-- schema 		: iam	    	- Identity and Access Management Madule
-- table 		: user_detail_h	- HISTORY :  Application users details
-- table alias  : usrdtl_h

-- schema section -------------------------------------------------

-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;

-- table section -------------------------------------------------
create table iam.user_detail_h (
	
	id 			character varying (256) not null,
	reg_id		character varying (39),					--	RID of indivisuals and referenced from idrepo database 
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
	del_dtimes  timestamp,
	
	eff_dtimes timestamp not null		-- for history record maintenance including the latest record in base table.

)
;

-- keys section -------------------------------------------------
 alter table iam.user_detail_h add constraint pk_usrdtl_h_id primary key (id, eff_dtimes)
 ;
 
-- indexes section -------------------------------------------------
-- create index idx_usrdtl_h_<ColX> on iam.user_detail_h (<ColX>)
-- ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE iam.user_detail_h IS 'User Detail History : This to track changes to iam record whenever there is an INSERT/UPDATE/DELETE ( soft delete ), Effective DateTimestamp is used for identifying latest or point in time information. Refer iam.user_detail table description for details.' ;

COMMENT ON COLUMN iam.user_detail_h.id IS 'User ID : Unique ID generated / assigned for a user' ;
COMMENT ON COLUMN iam.user_detail_h.reg_id IS 'Registration ID: RID of the user. Typically this will be used for authentication and validation of users' ;
COMMENT ON COLUMN iam.user_detail_h.name IS 'Name : User name' ;
COMMENT ON COLUMN iam.user_detail_h.email IS 'Email: Email address of the user' ;
COMMENT ON COLUMN iam.user_detail_h.mobile IS 'Mobile: Mobile number of the user' ;
COMMENT ON COLUMN iam.user_detail_h.status_code IS 'Status Code: User status. Refers to master.status_list.code' ;
COMMENT ON COLUMN iam.user_detail_h.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN iam.user_detail_h.last_login_dtimes IS 'Last Login Datetime: Date and time of the last login by the user' ;
COMMENT ON COLUMN iam.user_detail_h.last_login_method IS 'Last Login Method: Previous login method in which the user logged into the system' ;

COMMENT ON COLUMN iam.user_detail_h.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.user_detail_h.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.user_detail_h.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.user_detail_h.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.user_detail_h.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.user_detail_h.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.user_detail_h.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;
COMMENT ON COLUMN iam.user_detail_h.eff_dtimes IS 'Effective Date Timestamp : This to track iam record whenever there is an INSERT/UPDATE/DELETE ( soft delete ).  The current record is effective from this date-time. ' ;
