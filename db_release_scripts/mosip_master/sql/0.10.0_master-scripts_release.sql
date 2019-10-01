-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Release Version 	: 1.0
-- Purpose    		: Database Alter scripts for the release for Master DB.       
-- Create By   		: Sadanandegowda
-- Created Date		: 06-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_master sysadmin

\ir ../ddl/master-daysofweek_list.sql
\ir ../ddl/master-reg_working_nonworking.sql
\ir ../ddl/master-reg_exceptional_holiday.sql

-- object: fk_rwn_daycode | type: CONSTRAINT --
-- ALTER TABLE master.reg_working_nonworking DROP CONSTRAINT IF EXISTS fk_rwn_daycode CASCADE;
ALTER TABLE master.reg_working_nonworking ADD CONSTRAINT fk_rwn_daycode FOREIGN KEY (day_code,lang_code)
REFERENCES master.daysofweek_list (code,lang_code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_rwn_regcntr | type: CONSTRAINT --
-- ALTER TABLE master.reg_working_nonworking DROP CONSTRAINT IF EXISTS fk_rwn_regcntr CASCADE;
ALTER TABLE master.reg_working_nonworking ADD CONSTRAINT fk_rwn_regcntr FOREIGN KEY (regcntr_id,lang_code)
REFERENCES master.registration_center (id,lang_code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_regeh_regcntr | type: CONSTRAINT --
-- ALTER TABLE master.reg_exceptional_holiday DROP CONSTRAINT IF EXISTS fk_regeh_regcntr CASCADE;
ALTER TABLE master.reg_exceptional_holiday ADD CONSTRAINT fk_regeh_regcntr FOREIGN KEY (regcntr_id,lang_code)
REFERENCES master.registration_center (id,lang_code) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;


-------------- Level 1 data load scripts ------------------------

----- TRUNCATE master.daysofweek_list TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.daysofweek_list cascade ;

\COPY master.daysofweek_list (code,name,day_seq,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-daysofweek_list.csv' delimiter ',' HEADER  csv;


-------------- Level 2 data load scripts ------------------------

----- TRUNCATE master.reg_working_nonworking TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.reg_working_nonworking cascade ;

\COPY master.reg_working_nonworking (regcntr_id,day_code,lang_code,is_working,is_active,cr_by,cr_dtimes) FROM './dml/master-reg_working_nonworking.csv' delimiter ',' HEADER  csv;

----- TRUNCATE master.reg_exceptional_holiday TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.reg_exceptional_holiday cascade ;

\COPY master.reg_exceptional_holiday (regcntr_id,hol_date,hol_name,hol_reason,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-reg_exceptional_holiday.csv' delimiter ',' HEADER  csv;


----- TRUNCATE master.template TABLE Data and It's reference Data and COPY Data from CSV file -----
TRUNCATE TABLE master.template cascade ;

\COPY master.template (id,name,descr,file_format_code,model,file_txt,module_id,module_name,template_typ_code,lang_code,is_active,cr_by,cr_dtimes) FROM './dml/master-template.csv' delimiter ',' HEADER  csv;






