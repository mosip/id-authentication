-- Table: ida.batch_step_execution_context

-- DROP TABLE ida.batch_step_execution_context;

CREATE TABLE ida.batch_step_execution_context
(
        STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    	SERIALIZED_CONTEXT TEXT ,
    	constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
    	references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

-- Optimize autovacuum for batch_step_execution_context to clean dead tuples
ALTER TABLE batch_step_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 5000
);