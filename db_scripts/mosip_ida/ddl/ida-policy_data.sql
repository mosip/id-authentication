-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.policy_data

-- Purpose    	: 
--           
-- Create By   	: Ram Bhatt
-- Created Date	: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- Sep-2021		Ram Bhatt	    Added index to policy_id column
-- ------------------------------------------------------------------------------------------
-- object: ida.policy_data | type: TABLE --
-- DROP TABLE IF EXISTS ida.policy_data CASCADE;
CREATE TABLE ida.policy_data (
	policy_id character varying(36) NOT NULL,
	policy_data bytea NOT NULL,
	policy_name character varying(128) NOT NULL,
	policy_status character varying(36) NOT NULL,
	policy_description character varying(256),
	policy_commence_on timestamp NOT NULL,
	policy_expires_on timestamp,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT policy_data_pk PRIMARY KEY (policy_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_pd_pid ON ida.policy_data (policy_id);
--index section ends------
