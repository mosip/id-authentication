-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_reg
-- Table Name 	: reg.daysofweek_list
-- Purpose    	: Days of Week List : Stores all days of the week with Code and Name. The Days of week are kept with multiple language based on country configured languages..
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 04-Sep-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- object: reg.daysofweek_list | type: TABLE --
-- DROP TABLE IF EXISTS reg.daysofweek_list CASCADE;
CREATE TABLE reg.daysofweek_list(
	code character varying(3) NOT NULL,
	name character varying(36) NOT NULL,
	lang_code character varying(3) NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_daysofweek_code PRIMARY KEY (code,lang_code)

);


