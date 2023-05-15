-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.misp_license_data
-- Purpose    	: misp_license_data : 
--           
-- Created By   : Ram Bhatt
-- Created Date	: Apr-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- DROP TABLE IF EXISTS ida.misp_license_data CASCADE;
CREATE TABLE ida.misp_license_data (
	misp_id character varying(36) NOT NULL,
	license_key character varying(128) NOT NULL,
	misp_commence_on timestamp NOT NULL,
	misp_expires_on timestamp,
	misp_status character varying(36) NOT NULL,
	policy_id character varying(50),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT misp_license_data_pk PRIMARY KEY (misp_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_mld_lk ON ida.misp_license_data (license_key);
--index section ends------

