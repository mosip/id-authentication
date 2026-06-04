-- Table: ida.batch_job_execution_params

-- DROP TABLE ida.batch_job_execution_params;

CREATE TABLE ida.batch_job_execution_params  (
	    JOB_EXECUTION_ID BIGINT NOT NULL ,
    	PARAMETER_NAME VARCHAR(100) NOT NULL ,
    	PARAMETER_TYPE VARCHAR(100) NOT NULL ,
    	PARAMETER_VALUE VARCHAR(2500) ,
    	IDENTIFYING CHAR(1) NOT NULL ,
    	constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
    	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

-- Optimize autovacuum for batch_job_execution_params to clean dead tuples
ALTER TABLE batch_job_execution_params SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);