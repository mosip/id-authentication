-- --------------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Release Version 	: 0.10.1
-- Purpose    		: Revoking Database Alter deployement done for release in Master DB.       
-- Create By   		: Sadanandegowda
-- Created Date		: 20-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -----------------------------------------------------------------------------------------------------------
-- 29-Oct-2019          Sadanandegowda      Added device registration and device provider related DB changes
-- ------------------------------------------------------------------------------------------------------------
\c mosip_master sysadmin

--------- REVOKE DEVICE REGISTRATION, DEVICE PROVIDER, TRUST PROVIDER REQUIRED DB CHANGES -----------

------- TRUNCATE TABLE Data and It's reference Data loaded for release ----------
TRUNCATE TABLE master.reg_device_type cascade ;
TRUNCATE TABLE master.reg_device_sub_type cascade ;
TRUNCATE TABLE master.mosip_device_service cascade ;
TRUNCATE TABLE master.mosip_device_service_h cascade ;
TRUNCATE TABLE master.device_provider cascade ;
TRUNCATE TABLE master.device_provider_h cascade ;
TRUNCATE TABLE master.foundational_trust_provider cascade ;
TRUNCATE TABLE master.foundational_trust_provider_h cascade ;
TRUNCATE TABLE master.registered_device_master cascade ;
TRUNCATE TABLE master.registered_device_master_h cascade ;

-------- DROP Constraints on the new tables created for release ------------------
ALTER TABLE master.reg_device_sub_type DROP CONSTRAINT fk_rdstyp_dtype_code;
ALTER TABLE master.mosip_device_service DROP CONSTRAINT fk_mds_devprd;
ALTER TABLE master.mosip_device_service DROP CONSTRAINT fk_mds_dtype;
ALTER TABLE master.mosip_device_service DROP CONSTRAINT fk_mds_dstype;
ALTER TABLE master.registered_device_master DROP CONSTRAINT fk_regdevm_devprd;
ALTER TABLE master.registered_device_master DROP CONSTRAINT fk_regdevm_dtype;
ALTER TABLE master.registered_device_master DROP CONSTRAINT fk_regdevm_dstype;

DROP TABLE IF EXISTS master.reg_device_type;
DROP TABLE IF EXISTS master.reg_device_sub_type;
DROP TABLE IF EXISTS master.mosip_device_service;
DROP TABLE IF EXISTS master.mosip_device_service_h;
DROP TABLE IF EXISTS master.device_provider;
DROP TABLE IF EXISTS master.device_provider_h;
DROP TABLE IF EXISTS master.foundational_trust_provider;
DROP TABLE IF EXISTS master.foundational_trust_provider_h;
DROP TABLE IF EXISTS master.registered_device_master;
DROP TABLE IF EXISTS master.registered_device_master_h;

---------------------- DROP Tables created for release ----------------------------
------------------------------------------ END OF REVOKE --------------------------------------------