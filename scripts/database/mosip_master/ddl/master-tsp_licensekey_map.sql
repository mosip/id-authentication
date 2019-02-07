-- create table section -------------------------------------------------
-- schema 		: master	   		 		- Master reference Module
-- table 		: tsp_licensekey_map	    - List of TSP and Licenses mapping key table
-- table alias  : tsplkeym

-- schemas section -------------------------------------------------

-- create schema if master schema for Master reference Module is not exists
create schema if not exists master
;

-- table section -------------------------------------------------
create table master.tsp_licensekey_map (
	
	tsp_id 		character varying (36) not null, 
	license_key	character varying (255) not null, 	-- Refers to master.licensekey_list.license_key

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
 alter table master.tsp_licensekey_map add constraint pk_tsplkeym primary key (tsp_id, license_key) ;


-- comments section ------------------------------------------------- 
COMMENT ON TABLE master.tsp_licensekey_map IS 'TSP License : List of TSP and License keys mapping as per the security and access rights required, that will be assigned to specific trusted service provider.';

COMMENT ON COLUMN master.tsp_licensekey_map.tsp_id IS 'TSP ID : Unique ID generated / assigned for a trusted service provider';
COMMENT ON COLUMN master.tsp_licensekey_map.license_key IS 'License Key: Unique license keys generated to assign to trusted service provider and these license key will have authentication permissions assigned, Refered to master.licensekey_list.license_key';

COMMENT ON COLUMN master.tsp_licensekey_map.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN master.tsp_licensekey_map.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN master.tsp_licensekey_map.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN master.tsp_licensekey_map.upd_by IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN master.tsp_licensekey_map.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN master.tsp_licensekey_map.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN master.tsp_licensekey_map.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;


