-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.otp_transaction
-- Purpose    	: OTP Transaction: All OTP related data and validation details are maintained here for ID Authentication.
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: Sep-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------
-- object: ida.otp_transaction | type: TABLE --
-- DROP TABLE IF EXISTS ida.otp_transaction CASCADE;
CREATE TABLE ida.otp_transaction(
	id character varying(36) NOT NULL,
	ref_id character varying(64) NOT NULL,
	otp_hash character varying(512) NOT NULL,
	generated_dtimes timestamp,
	expiry_dtimes timestamp,
	validation_retry_count smallint,
	status_code character varying(36),
	lang_code character varying(3),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_otpt_id PRIMARY KEY (id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_refid ON ida.otp_transaction (ref_id,status_code);
--index section ends------
COMMENT ON TABLE ida.otp_transaction IS 'OTP Transaction: All OTP related data and validation details are maintained here for ID Authentication module.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.id IS 'ID: Key alias id is a unique identifier (UUID) used as an alias of the encryption key stored in keystore like HSM (hardware security module).';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.ref_id IS 'Reference ID: Reference ID is a reference information received from OTP requester which can be used while validating the OTP. AM: please give examples of ref_id';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.otp_hash IS 'OTP Hash: Hash of id, ref_id and otp which is generated based on the configuration setup and sent to the requester application / module.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.generated_dtimes IS 'Generated Date Time: Date and Time when the OTP was generated';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.expiry_dtimes IS 'Expiry Date Time: Date Time when the OTP will be expired';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.validation_retry_count IS 'Validation Retry Count: Validation retry counts of this OTP request. If the validation retry crosses the threshold limit, then the OTP will be de-activated.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.status_code IS 'Status Code: Status of the OTP whether it is active or expired. AM: please enumerate the status types. They are only a few, not infinite';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN ida.otp_transaction.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
