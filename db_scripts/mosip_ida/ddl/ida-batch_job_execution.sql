-- Table: ida.batch_job_execution

-- DROP TABLE ida.batch_job_execution;

CREATE TABLE ida.batch_job_execution  (
    JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
  	VERSION BIGINT  ,
  	JOB_INSTANCE_ID BIGINT NOT NULL,
  	CREATE_TIME TIMESTAMP NOT NULL,
  	START_TIME TIMESTAMP DEFAULT NULL ,
  	END_TIME TIMESTAMP DEFAULT NULL ,
  	STATUS VARCHAR(10) ,
  	EXIT_CODE VARCHAR(2500) ,
  	EXIT_MESSAGE VARCHAR(2500) ,
  	LAST_UPDATED TIMESTAMP,
  	constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
  	references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) 
WITH (
    OIDS = FALSE
);

-- Optimize autovacuum for batch_job_execution to clean dead tuples
ALTER TABLE batch_job_execution SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

--PERFORMANCE INDEXES
CREATE INDEX IF NOT EXISTS idx_job_exec_instance ON ida.batch_job_execution USING btree (job_instance_id);