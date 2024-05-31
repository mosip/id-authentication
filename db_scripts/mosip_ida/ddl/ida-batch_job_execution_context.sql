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
