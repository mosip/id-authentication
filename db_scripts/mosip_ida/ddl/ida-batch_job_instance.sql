-- Table: ida.batch_job_instance 

-- DROP TABLE ida.batch_job_instance;

CREATE TABLE ida.batch_job_instance  (
    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
 	VERSION BIGINT ,
 	JOB_NAME VARCHAR(100) NOT NULL,
 	JOB_KEY VARCHAR(32) NOT NULL,
 	constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
)
WITH (
    OIDS = FALSE
);