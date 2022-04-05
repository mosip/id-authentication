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

-- object: STEP_EXEC_CTX_FK | type: CONSTRAINT --
-- ALTER TABLE ida.batch_step_execution_context DROP CONSTRAINT IF EXISTS STEP_EXEC_CTX_FK CASCADE;
ALTER TABLE ida.batch_step_execution_context ADD CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID);
-- ddl-end --  

-- object: JOB_EXEC_CTX_FK | type: CONSTRAINT --  
-- ALTER TABLE ida.batch_job_execution_context DROP CONSTRAINT IF EXISTS JOB_EXEC_CTX_FK CASCADE;
ALTER TABLE ida.batch_job_execution_context ADD CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID);
-- ddl-end --  

-- object: JOB_INSTANCE_EXECUTION_FK | type: CONSTRAINT --
-- ALTER TABLE ida.batch_job_execution DROP CONSTRAINT IF EXISTS JOB_INSTANCE_EXECUTION_FK CASCADE;
ALTER TABLE ida.batch_job_execution ADD CONSTRAINT JOB_INSTANCE_EXECUTION_FK FOREIGN KEY (JOB_INSTANCE_ID)
REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID);
-- ddl-end --  

-- object: JOB_EXECUTION_STEP_FK | type: CONSTRAINT --
-- ALTER TABLE ida.batch_step_execution DROP CONSTRAINT IF EXISTS JOB_EXECUTION_STEP_FK CASCADE;
ALTER TABLE ida.batch_step_execution ADD CONSTRAINT JOB_EXECUTION_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID);
-- ddl-end -- 

-- object: JOB_EXEC_PARAMS_FK | type: CONSTRAINT --
-- ALTER TABLE ida.batch_job_execution_params DROP CONSTRAINT IF EXISTS JOB_EXEC_PARAMS_FK CASCADE;
ALTER TABLE ida.batch_job_execution_params ADD CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID);
-- ddl-end --

CREATE SEQUENCE ida.batch_job_seq;
CREATE SEQUENCE ida.batch_job_execution_seq;
CREATE SEQUENCE ida.batch_step_execution_seq;

-- grants to access all sequences
GRANT usage, SELECT ON ALL SEQUENCES 
   IN SCHEMA ida
   TO idauser;