-- ---------------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Release Version 	: 0.10.2
-- Purpose    		: Database Alter scripts for the release for Master DB.       
-- Create By   		: Sadanandegowda
-- Created Date		: 06-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -----------------------------------------------------------------------------------------------------------
-- 29-Oct-2019          Sadanandegowda      Added device registration and device provider related DB changes

-- ------------------------------------------------------------------------------------------------------------
\c mosip_master sysadmin

---------------- DEVICE REGISTRATION, DEVICE PROVIDER, TRUST PROVIDER REQUIRED DB CHANGES ------------------

---------------- DDL DEPLOYMENT  ------------------

\ir ../ddl/master-reg_device_type.sql
\ir ../ddl/master-reg_device_sub_type.sql
\ir ../ddl/master-mosip_device_service.sql
\ir ../ddl/master-mosip_device_service_h.sql
\ir ../ddl/master-device_provider.sql
\ir ../ddl/master-device_provider_h.sql
\ir ../ddl/master-foundational_trust_provider.sql
\ir ../ddl/master-foundational_trust_provider_h.sql

DROP TABLE IF EXISTS master.registered_device_master;
\ir ../ddl/master-registered_device_master.sql

DROP TABLE IF EXISTS master.registered_device_master_h;
\ir ../ddl/master-registered_device_master_h.sql

------ FORIEGN KEY CONSTRAINTS CREATION  ------------

-- object: fk_rdstyp_dtype_code | type: CONSTRAINT --
-- ALTER TABLE master.reg_device_sub_type DROP CONSTRAINT IF EXISTS fk_rdstyp_dtype_code CASCADE;
ALTER TABLE master.reg_device_sub_type ADD CONSTRAINT fk_rdstyp_dtype_code FOREIGN KEY (dtyp_code)
REFERENCES master.reg_device_type (code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_mds_devprd | type: CONSTRAINT --
-- ALTER TABLE master.mosip_device_service DROP CONSTRAINT IF EXISTS fk_mds_devprd CASCADE;
ALTER TABLE master.mosip_device_service ADD CONSTRAINT fk_mds_devprd FOREIGN KEY (dprovider_id)
REFERENCES master.device_provider (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_mds_dtype | type: CONSTRAINT --
-- ALTER TABLE master.mosip_device_service DROP CONSTRAINT IF EXISTS fk_mds_dtype CASCADE;
ALTER TABLE master.mosip_device_service ADD CONSTRAINT fk_mds_dtype FOREIGN KEY (dtype_code)
REFERENCES master.reg_device_type (code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_mds_dstype | type: CONSTRAINT --
-- ALTER TABLE master.mosip_device_service DROP CONSTRAINT IF EXISTS fk_mds_dstype CASCADE;
ALTER TABLE master.mosip_device_service ADD CONSTRAINT fk_mds_dstype FOREIGN KEY (dstype_code)
REFERENCES master.reg_device_sub_type (code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


-- object: fk_regdevm_devprd | type: CONSTRAINT --
-- ALTER TABLE master.registered_device_master DROP CONSTRAINT IF EXISTS fk_regdevm_devprd CASCADE;
ALTER TABLE master.registered_device_master ADD CONSTRAINT fk_regdevm_devprd FOREIGN KEY (provider_id)
REFERENCES master.device_provider (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_regdevm_dtype | type: CONSTRAINT --
-- ALTER TABLE master.registered_device_master DROP CONSTRAINT IF EXISTS fk_regdevm_dtype CASCADE;
ALTER TABLE master.registered_device_master ADD CONSTRAINT fk_regdevm_dtype FOREIGN KEY (dtype_code)
REFERENCES master.reg_device_type (code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_regdevm_dstype | type: CONSTRAINT --
-- ALTER TABLE master.registered_device_master DROP CONSTRAINT IF EXISTS fk_regdevm_dstype CASCADE;
ALTER TABLE master.registered_device_master ADD CONSTRAINT fk_regdevm_dstype FOREIGN KEY (dstype_code)
REFERENCES master.reg_device_sub_type (code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

---------------- DML DEPLOYMENT  ------------------

----- TRUNCATE master.template TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.template cascade ;

\COPY master.template (id,name,descr,file_format_code,model,file_txt,module_id,module_name,template_typ_code,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-template.csv' delimiter ',' HEADER  csv;

----- TRUNCATE master.reg_device_type TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.reg_device_type cascade ;

\COPY master.reg_device_type (code,name,descr,is_active,cr_by,cr_dtimes) FROM './dml/master-reg_device_type.csv' delimiter ',' HEADER  csv;

----- TRUNCATE master.reg_device_sub_type TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.reg_device_sub_type cascade ;

\COPY master.reg_device_sub_type (code,dtyp_code,name,descr,is_active,cr_by,cr_dtimes) FROM './dml/master-reg_device_sub_type.csv' delimiter ',' HEADER  csv;

--------------------------------------------------------------------------------------------------------------------------------------------------------
