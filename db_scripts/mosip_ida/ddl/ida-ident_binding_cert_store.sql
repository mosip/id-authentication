-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.ident_binding_cert_store
-- Purpose    	: ident_binding_cert_store : To store Identity binding certificates.
--           
-- Created By   : Mahammed Taheer
-- Created Date	: Jan-2023
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- DROP TABLE IF EXISTS ida.ident_binding_cert_store CASCADE;
CREATE TABLE ida.ident_binding_cert_store (
	cert_id character varying(36) NOT NULL,
	id_vid_hash character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	certificate_data character varying NOT NULL,
	public_key_hash character varying(1024) NOT NULL,
	cert_thumbprint character varying(100) NOT NULL,
	partner_name character varying(128) NOT NULL,
	auth_factor character varying(100) NOT NULL,
	cert_expire timestamp NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT uni_public_key_hash_const UNIQUE (public_key_hash)
);
-- ddl-end --