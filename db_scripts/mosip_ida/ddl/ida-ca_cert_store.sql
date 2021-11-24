-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.ca_cert_store
-- Purpose    	: Certificate Authority Certificate Store: Store details of all the certificate provided by certificate authority which will be used by MOSIP
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: Dec-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- Jan-2021		Ram Bhatt	    Set is_deleted flag to not null and default false
-- Mar-2021		Ram Bhatt	    Reverting is_deleted not null changes
-- ------------------------------------------------------------------------------------------

-- object: ida.ca_cert_store | type: TABLE --
-- DROP TABLE IF EXISTS ida.ca_cert_store CASCADE;
CREATE TABLE ida.ca_cert_store(
	cert_id character varying(36) NOT NULL,
	cert_subject character varying(500) NOT NULL,
	cert_issuer character varying(500) NOT NULL,
	issuer_id character varying(36) NOT NULL,
	cert_not_before timestamp,
	cert_not_after timestamp,
	crl_uri character varying(120),
	cert_data character varying,
	cert_thumbprint character varying(100),
	cert_serial_no character varying(50),
	partner_domain character varying(36),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_cacs_id PRIMARY KEY (cert_id),
	CONSTRAINT cert_thumbprint_unique UNIQUE (cert_thumbprint,partner_domain)

);
-- ddl-end --
COMMENT ON TABLE ida.ca_cert_store IS 'Certificate Authority Certificate Store: Store details of all the certificate provided by certificate authority which will be used by MOSIP';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_id IS 'Certificate ID: Unique ID (UUID) will be generated and assigned to the uploaded CA/Sub-CA certificate';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_subject IS 'Certificate Subject: Subject DN of the certificate';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_issuer IS 'Certificate Issuer: Issuer DN of the certificate';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.issuer_id IS 'Issuer UUID of the certificate. (Issuer certificate should be available in the DB)';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_not_before IS 'Certificate Start Date: Certificate Interval - Validity Start Date & Time';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_not_after IS 'Certificate Validity end Date: Certificate Interval - Validity End Date & Time';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.crl_uri IS 'CRL URL: CRL URI of the issuer.';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_data IS 'Certificate Data: PEM Encoded actual certificate data.';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_thumbprint IS 'Certificate Thumb Print: SHA1 generated certificate thumbprint';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cert_serial_no IS 'Certificate Serial No: Serial Number of the certificate.';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.partner_domain IS 'Partner Domain : To add Partner Domain in CA/Sub-CA certificate chain';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN ida.ca_cert_store.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
