-- create table section -------------------------------------------------
-- schema 		: master	   		 		- Master reference Module
-- table 		: licensekey_permission	    - License Key and their permission mapping
-- table alias  : lkeyper

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.licensekey_permission (
	
	license_key		character varying (255) not null,   -- Refers to master.licensekey_list.license_key
	permission  	character varying (255) not null, 

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
 alter table master.licensekey_permission add constraint pk_lkeyper primary key (license_key) ;

-- comments section ------------------------------------------------- 
COMMENT ON TABLE master.licensekey_permission IS 'TSP License keys permissions: List of TSP License keys and Authentication method mapping as per the security and access rights required, Required authentication types will be assigned to License key';

COMMENT ON COLUMN master.licensekey_permission.license_key IS 'License Key: Unique license keys generated to assign to trusted service provider and these license number will have authentication permissions assigned, Refered to master.licensekey_list.license_key';
COMMENT ON COLUMN master.licensekey_permission.permission IS 'Permission: Unique authentication permissions supported by the authentication system, for ex., OTP, BIO, DEMOGRAPHIC, eKYC etc. Refered from config server';

COMMENT ON COLUMN master.licensekey_permission.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN master.licensekey_permission.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN master.licensekey_permission.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN master.licensekey_permission.upd_by IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN master.licensekey_permission.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN master.licensekey_permission.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN master.licensekey_permission.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;


