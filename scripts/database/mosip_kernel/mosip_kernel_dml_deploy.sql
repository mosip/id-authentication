\c mosip_kernel sysadmin

\set CSVDataPath '\'/home/dbadmin/mosip_kernel/'

-------------- Level 1 data load scripts ------------------------

----- TRUNCATE kernel.key_policy_def TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE kernel.key_policy_def cascade ;

\COPY kernel.key_policy_def (app_id,key_validity_duration,is_active,cr_by,cr_dtimes) FROM './dml/key_policy_def.csv' delimiter ',' HEADER  csv;


----- TRUNCATE kernel.key_policy_def_h TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE kernel.key_policy_def_h cascade ;

\COPY kernel.key_policy_def_h (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,eff_dtimes) FROM './dml/key_policy_def_h.csv' delimiter ',' HEADER  csv;


---------------------------------------------------------------------------------------------------------------------------------------------------------------------


















