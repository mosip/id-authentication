-- create table section -------------------------------------------------
-- schema 		: iam	    			- Identity and Access Management Madule
-- table 		: oauth_access_token 	- List of Auth access tokens for Authentication
-- table alias  : authat		

-- schemas section -------------------------------------------------
 
-- create schema if iam schema for IAM is not exists
create schema if not exists iam
;

-- table section -------------------------------------------------
create table iam.oauth_access_token  (
	
	auth_token		character varying(1024),
	user_id 		character varying(256),
	refresh_token 	character varying(1024),
	expiration_time timestamp,

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
 alter table iam.oauth_access_token  add constraint pk_authat_id primary key (user_id)
 ;

-- comments section ------------------------------------------------- 

COMMENT ON TABLE iam.oauth_access_token IS 'Authentication Access Token : This table is used to store the auth token, refresh token and expiration time for JWT token based validation.' ;

COMMENT ON COLUMN iam.oauth_access_token.auth_token IS 'Authentication Token : JWT Token for user logged in';
COMMENT ON COLUMN iam.oauth_access_token.user_id IS 'User ID: User Id of the user logged in' ;
COMMENT ON COLUMN iam.oauth_access_token.refresh_token IS 'Refresh Token : JWT Refresh token when auth token expires' ;
COMMENT ON COLUMN iam.oauth_access_token.expiration_time IS 'Expiration Time : Expiration time of Auth Token' ;

COMMENT ON COLUMN iam.oauth_access_token.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN iam.oauth_access_token.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN iam.oauth_access_token.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN iam.oauth_access_token.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN iam.oauth_access_token.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN iam.oauth_access_token.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN iam.oauth_access_token.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;