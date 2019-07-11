-- create table section --------------------------------------------------------
-- schema 		: master  			- Master Reference schema
-- table 		: individual_type  	- Individual type table
-- table alias  : indvtyp	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
  
-- table section -------------------------------------------------------------------------------

create table master.individual_type (
	code 		character varying(36) not null,     
	name		character varying(64) not null,

	lang_code  	character varying(3) not null,		-- master.language.code

	is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (256),
	upd_dtimes  timestamp ,
	is_deleted 	boolean,
	del_dtimes	timestamp 
)
;

-- keys section -------------------------------------------------------------------------------
alter table master.individual_type add constraint pk_indvtyp_code primary key (code, lang_code);

-- comments section -------------------------------------------------------------------------- 
COMMENT ON TABLE master.individual_type is 'Individual Type : Type of indivisuals considered during registration.for ex., foreigner, non-foreigner, etc.';

COMMENT ON COLUMN master.individual_type.code IS 'Individual Type Code : Individual type code for ex., FR, NFR etc.';
COMMENT ON COLUMN master.individual_type.name IS 'Individual Type Name: Individual type name for ex., foreigner, non-foreigner ';

COMMENT ON COLUMN master.individual_type.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language. ' ;
COMMENT ON COLUMN master.individual_type.is_active IS 'IS_Active : Flag to mark whether the record is Active or In-active' ;
COMMENT ON COLUMN master.individual_type.cr_by IS 'Created By : ID or name of the user who create / insert record' ;
COMMENT ON COLUMN master.individual_type.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted' ;
COMMENT ON COLUMN master.individual_type.upd_by IS 'Updated By : ID or name of the user who update the record with new values' ;
COMMENT ON COLUMN master.individual_type.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.' ;
COMMENT ON COLUMN master.individual_type.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.' ;
COMMENT ON COLUMN master.individual_type.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE' ;
