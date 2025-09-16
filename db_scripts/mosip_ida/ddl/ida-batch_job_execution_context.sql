-- Table: ida.batch_job_execution_context

-- DROP TABLE ida.batch_job_execution_context;

CREATE TABLE ida.batch_job_execution_context
(
        JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    	SERIALIZED_CONTEXT TEXT ,
    	constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
    	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

-- Optimize autovacuum for batch_job_execution_context to clean dead tuples
ALTER TABLE batch_job_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);