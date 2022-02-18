\c mosip_ida 

TRUNCATE TABLE ida.key_policy_def cascade ;

\COPY ida.key_policy_def (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,pre_expire_days,access_allowed) FROM './dml/ida-key_policy_def.csv' delimiter ',' HEADER  csv;

TRUNCATE TABLE ida.key_policy_def_h cascade ;

\COPY ida.key_policy_def_h (app_id,key_validity_duration,is_active,cr_by,cr_dtimes,eff_dtimes) FROM './dml/ida-key_policy_def_h.csv' delimiter ',' HEADER  csv;

















