-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Table Name 	: master.registered_device_master_h
-- Purpose    	: Registered Device Master History : History- Contains list of registered devices and details,  like fingerprint scanner, iris scanner, scanner etc used at registration centers, authentication services, eKYC...etc. Table is created temporarily and will be relooked in to this for re-design of the solution later.
--           
-- Create By   	: Nasir Khan / Sadanandegowda
-- Created Date	: 30-Jul-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- 
-- ------------------------------------------------------------------------------------------
-- object: master.registered_device_master_h | type: TABLE --
-- DROP TABLE IF EXISTS master.registered_device_master_h CASCADE;
CREATE TABLE master.registered_device_master_h(
	code character varying(36) NOT NULL,
	type character varying(64),
	subtype character varying(64),
	status_code character varying(36),
	device_id character varying(36) NOT NULL,
	device_sub_id character varying(1024),
	provider_id character varying(36) NOT NULL,
	provider_name character varying(128),
	mosip_process character varying(64) NOT NULL,
	firmware character varying(128),
	make character varying(36),
	model character varying(36),
	expiry_date timestamp,
	certification bytea,
	foundational_trust_provider_id character varying(36),
	foundational_trust_signature character varying(512),
	foundational_trust_certificate bytea,
	dpsignature character varying(512),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	eff_dtimes timestamp NOT NULL,
	CONSTRAINT pk_regdevicemh_code PRIMARY KEY (code,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE master.registered_device_master_h IS 'Registered Device Master History : History- Contains list of registered devices and details,  like fingerprint scanner, iris scanner, scanner etc used at registration centers, authentication services, eKYC...etc. Table is created temporarily and will be relooked in to this for re-design of the solution later.';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.code IS 'Device ID : Unique ID generated / assigned for device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.type IS 'Type: Types of devices used for registration processes, authentication, eKYC..etc. for ex., printer, scanner, etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.subtype IS 'Sub Type: Sub types of devices used for registration processes, authentication, eKYC..etc. for ex., thumb finger print, palm print scanner, Iris scanner, etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.status_code IS ' Status Code : Status of the registered devices';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.device_id IS 'Device ID: Unique ID of the device by the provider, This ID can be serial number device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.device_sub_id IS 'Device Sub ID: Sub ID of the devices, Each device will have an array of sub IDs';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.provider_id IS 'Provider ID: ID of the device provider';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.provider_name IS 'Provider Name: Name of the device provider';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.mosip_process IS 'MOSIP process where there devices will be used for. ex. Registrations, Authentication, eKYC...etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.firmware IS 'Firmware: Firmware used in devices';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.make IS 'Make: Make of the device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.expiry_date IS 'Expiry Date: expiry date of the device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.certification IS 'Certification: Device certification';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.foundational_trust_provider_id IS 'Foundational Trust Provider ID: Foundational trust provider ID';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.foundational_trust_signature IS 'Foundational Trust Signature: Signature of the foundational trust';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.foundational_trust_certificate IS 'Foundational Trust Certificate: Certificate of the foundational trust';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.dpsignature IS 'DP Signation: DP Signature';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master_h.eff_dtimes IS 'Effective Date Timestamp : This to track master record whenever there is an INSERT/UPDATE/DELETE ( soft delete ).  The current record is effective from this date-time.';
-- ddl-end --
