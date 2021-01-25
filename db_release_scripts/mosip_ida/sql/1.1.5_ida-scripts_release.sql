-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.1.5
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Sadanandegowda DM
-- Created Date		: Jan-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_ida sysadmin

---------------- KEY MANAGER DDL DEPLOYMENT  ------------------

\ir ../ddl/ida-credential_event_store.sql


\ir ../ddl/ida-batch_job_execution.sql
\ir ../ddl/ida-batch_job_execution_context.sql
\ir ../ddl/ida-batch_job_execution_params.sql
\ir ../ddl/ida-batch_job_instance.sql
\ir ../ddl/ida-batch_step_execution.sql
\ir ../ddl/ida-batch_step_execution_context.sql

\ir ../ddl/ida-fk.sql

----------------------------------------------------------------------------------------------------