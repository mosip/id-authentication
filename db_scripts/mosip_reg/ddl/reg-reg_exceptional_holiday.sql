-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_reg
-- Table Name 	: reg.reg_exceptional_holiday
-- Purpose    	: Registration Center Exceptional Holiday : Table to store all the exceptioanl holidays declared for the registartion centers. Will have all details on the exceptional holiday details like data, reason for holiday and registration center details.
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 04-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- object: reg.reg_exceptional_holiday | type: TABLE --
-- DROP TABLE IF EXISTS reg.reg_exceptional_holiday CASCADE;
CREATE TABLE reg.reg_exceptional_holiday(
	regcntr_id character varying(10) NOT NULL,
	hol_date date NOT NULL,
	hol_name character varying(128),
	hol_reason character varying(256),
	lang_code character varying(3) NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_exceptional_hol PRIMARY KEY (regcntr_id,hol_date)

);