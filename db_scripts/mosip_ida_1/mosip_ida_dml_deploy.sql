\c mosip_ida_1 sysadmin

\set CSVDataPath '\'/home/dbadmin/mosip_ida'

-------------- Level 1 data load scripts ------------------------

----- TRUNCATE ida.key_policy_def TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def cascade ;

\COPY ida.key_policy_def (app_id,key_validity_duration,is_active,cr_by,cr_dtimes) FROM './dml/ida-key_policy_def.csv' delimiter ',' HEADER  csv;


----- TRUNCATE ida.key_policy_def_h TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE ida.key_policy_def_h cascade ;

\COPY ida.key_policy_def_h (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,eff_dtimes) FROM './dml/ida-key_policy_def_h.csv' delimiter ',' HEADER  csv;

---------------------------------------------------------------------------------------------------------------------------------------------------------------------


















