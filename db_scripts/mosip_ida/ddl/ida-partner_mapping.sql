-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.partner_mapping

-- Purpose    	: 
--           
-- Create By   	: Ram Bhatt
-- Created Date	: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------
-- object: ida.partner_mapping | type: TABLE --
-- DROP TABLE IF EXISTS ida.partner_mapping CASCADE;
CREATE TABLE ida.partner_mapping (
	partner_id character varying(36) NOT NULL,
	policy_id character varying(36) NOT NULL,
	api_key_id character varying(100) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_mapping_pk PRIMARY KEY (partner_id,policy_id,api_key_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_pm_pid ON ida.partner_mapping (partner_id);
--index section ends------
