-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.api_key_data

-- Purpose    	: 
--           
-- Create By   	: Ram Bhatt
-- Created Date	: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- Sep-2021		Ram Bhatt	    Added index to api_key_id column
-- ------------------------------------------------------------------------------------------
-- object: ida.api_key_data | type: TABLE --
-- DROP TABLE IF EXISTS ida.api_key_data CASCADE;
CREATE TABLE ida.api_key_data (
	api_key_id character varying(36) NOT NULL,
	api_key_commence_on timestamp NOT NULL,
	api_key_expires_on timestamp,
	api_key_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT api_key_data_pk PRIMARY KEY (api_key_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_akd_apkeyid ON ida.api_key_data (api_key_id);
--index section ends------
