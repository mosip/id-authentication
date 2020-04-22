-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Release Version 	: 1.0.8
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: 22-Apr-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

\ir ../ddl/ida-key_alias.sql
\ir ../ddl/ida-key_store.sql
\ir ../ddl/ida-key_policy_def.sql
\ir ../ddl/ida-key_policy_def_h.sql


-------------- Level 1 data load scripts ------------------------

----- TRUNCATE ida.key_policy_def TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def cascade ;

\COPY ida.key_policy_def (app_id,key_validity_duration,is_active,cr_by,cr_dtimes) FROM './dml/ida-key_policy_def.csv' delimiter ',' HEADER  csv;


----- TRUNCATE ida.key_policy_def_h TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def_h cascade ;

\COPY ida.key_policy_def_h (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,eff_dtimes) FROM './dml/ida-key_policy_def_h.csv' delimiter ',' HEADER  csv;

---------------------------------------------------------------------------------------------------------------------------------------------------------------------


----------------------------------------------------------------------------------------------------