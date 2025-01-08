-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name : 
-- Purpose    : All the FKs are created separately, not part of create table scripts to ease the deployment process
--           
-- Create By   : Sadanandegowda DM
-- Created Date: Jan-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- 
-- ------------------------------------------------------------------------------------------

CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

-- grants to access all sequences
GRANT usage, SELECT ON ALL SEQUENCES 
   IN SCHEMA ida
   TO idauser;