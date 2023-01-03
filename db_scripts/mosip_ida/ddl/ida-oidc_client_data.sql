-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.oidc_client_data
-- Purpose    	: oidc_client_data : To store OIDC client details.
--           
-- Created By   : Mahammed Taheer
-- Created Date	: Sept-2022
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- DROP TABLE IF EXISTS ida.oidc_client_data CASCADE;
CREATE TABLE ida.oidc_client_data (
	oidc_client_id character varying(100) NOT NULL,
	oidc_client_name character varying(128) NOT NULL,
	oidc_client_status character varying(36) NOT NULL,
	user_claims character varying(1024) NOT NULL,
	auth_context_refs character varying(1024) NOT NULL,
	client_auth_methods character varying(1024) NOT NULL,
	partner_id character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT oidc_client_data_pk PRIMARY KEY (oidc_client_id)

);
-- ddl-end --