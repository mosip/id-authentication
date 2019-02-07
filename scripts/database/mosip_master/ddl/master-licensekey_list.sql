-- create table section -------------------------------------------------
-- schema 		: master	    		- Master reference Module
-- table 		: licensekey_list		- Master List of License Keys
-- table alias  : lkeylst

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.licensekey_list (
	
	license_key	character varying (255) not null,
	
	created_dtime timestamp,
	expiry_dtime timestamp,
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table master.licensekey_list add constraint pk_lkeylst primary key (license_key) ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE master.licensekey_list IS 'License Key List : Master List of License keys which will be assigned to trusted service provider to give access rights to do individual authentication';

COMMENT ON COLUMN master.licensekey_list.license_key IS 'License Key: Unique license keys generated to assign to trusted service provider.';

COMMENT ON COLUMN master.licensekey_list.created_dtime IS 'Created Date and Time : License key created date time';
COMMENT ON COLUMN master.licensekey_list.expiry_dtime IS 'Expiry Date and Time : License key expiry date time';

COMMENT ON COLUMN master.licensekey_list.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN master.licensekey_list.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN master.licensekey_list.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN master.licensekey_list.upd_by IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN master.licensekey_list.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN master.licensekey_list.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN master.licensekey_list.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;


