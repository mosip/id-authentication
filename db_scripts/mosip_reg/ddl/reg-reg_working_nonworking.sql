-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_reg
-- Table Name 	: reg.reg_working_nonworking
-- Purpose    	: Registration Center Working NonWorking : Stores working and non-working days of the week for all registration centers. As per the requirement working and non-working days are defined at center level.
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 04-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- object: reg.reg_working_nonworking | type: TABLE --
-- DROP TABLE IF EXISTS reg.reg_working_nonworking CASCADE;
CREATE TABLE reg.reg_working_nonworking(
	regcntr_id character varying(10) NOT NULL,
	day_code character varying(3) NOT NULL,
	lang_code character varying(3) NOT NULL,
	is_working boolean NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_working_nonworking PRIMARY KEY (regcntr_id,day_code)

);


