-- ---------------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Release Version 	: 1.0.4
-- Purpose    		: Database Alter scripts for the release for Master DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 30-Dec-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -----------------------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------------------------
\c mosip_master sysadmin

ALTER TABLE master.registered_device_master DROP COLUMN IF EXISTS foundational_trust_signature;
ALTER TABLE master.registered_device_master DROP COLUMN IF EXISTS foundational_trust_certificate;
ALTER TABLE master.registered_device_master DROP COLUMN IF EXISTS dprovider_signature;

ALTER TABLE master.registered_device_master_h DROP COLUMN IF EXISTS foundational_trust_signature;
ALTER TABLE master.registered_device_master_h DROP COLUMN IF EXISTS foundational_trust_certificate;
ALTER TABLE master.registered_device_master_h DROP COLUMN IF EXISTS dprovider_signature;

-------------- Level 1 data load scripts ------------------------

----- TRUNCATE master.template_type TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.template_type cascade ;

\COPY master.template_type (code,descr,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-template_type.csv' delimiter ',' HEADER  csv;

-------------- Level 2 data load scripts ------------------------

----- TRUNCATE master.template TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.template cascade ;

\COPY master.template (id,name,descr,file_format_code,model,file_txt,module_id,module_name,template_typ_code,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-template.csv' delimiter ',' HEADER  csv;

