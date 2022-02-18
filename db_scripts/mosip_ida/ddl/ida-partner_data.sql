-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.partner_data

-- Purpose    	: 
--           
-- Create By   	: Ram Bhatt
-- Created Date	: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------
-- object: ida.partner_data | type: TABLE --
-- DROP TABLE IF EXISTS ida.partner_data CASCADE;
CREATE TABLE ida.partner_data (
	partner_id character varying(36) NOT NULL,
	partner_name character varying(128) NOT NULL,
	certificate_data bytea ,
	partner_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_data_pk PRIMARY KEY (partner_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_pd_pid ON ida.partner_data (partner_id);
--index section ends------
