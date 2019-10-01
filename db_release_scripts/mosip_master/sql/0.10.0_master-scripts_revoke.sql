-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Release Version 	: 1.0
-- Purpose    		: Revoking Database Alter deployement done for release in Master DB.       
-- Create By   		: Sadanandegowda
-- Created Date		: 20-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_master sysadmin


----- TRUNCATE TABLE Data and It's reference Data loaded for 1.0 release -----
TRUNCATE TABLE master.daysofweek_list cascade ;
TRUNCATE TABLE master.reg_working_nonworking cascade ;
TRUNCATE TABLE master.reg_exceptional_holiday cascade ;

----- DROP Constraints on the new tables created for 1.0 release -----
ALTER TABLE master.reg_working_nonworking DROP CONSTRAINT fk_rwn_daycode;
ALTER TABLE master.reg_working_nonworking DROP CONSTRAINT fk_rwn_regcntr;
ALTER TABLE master.reg_exceptional_holiday DROP CONSTRAINT fk_regeh_regcntr;

----- DROP Tables created for 1.0 rlease -----
DROP TABLE IF EXISTS master.daysofweek_list;
DROP TABLE IF EXISTS master.reg_working_nonworking;
DROP TABLE IF EXISTS master.reg_exceptional_holiday;

-------------------------------- END OF REVOKE ------------------------------