-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_regprc
-- Release Version 	: 1.0.2
-- Purpose    		: Database Alter scripts for the release for Registration Processor DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 11-Dec-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_regprc sysadmin

ALTER TABLE regprc.individual_demographic_dedup ADD COLUMN mobile_number character varying(64);

ALTER TABLE regprc.individual_demographic_dedup ADD COLUMN email character varying(512);

ALTER TABLE regprc.individual_demographic_dedup ADD COLUMN pincode character varying(64);