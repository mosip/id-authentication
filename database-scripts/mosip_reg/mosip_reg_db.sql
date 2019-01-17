DROP DATABASE IF EXISTS mosip_reg;
CREATE DATABASE mosip_reg
	ENCODING = 'UTF8'
	LC_COLLATE = 'en_US.UTF-8'
	LC_CTYPE = 'en_US.UTF-8'
	TABLESPACE = pg_default
	OWNER = sysadmin;
-- ddl-end --
COMMENT ON DATABASE mosip_reg IS 'Registration client database to capture registration related data. The needed data from MOSIP system will be synched with this database';
-- ddl-end --

\c mosip_reg sysadmin

-- object: reg | type: SCHEMA --
DROP SCHEMA IF EXISTS reg CASCADE;
CREATE SCHEMA reg;
-- ddl-end --
ALTER SCHEMA reg OWNER TO sysadmin;
-- ddl-end --

ALTER DATABASE mosip_reg SET search_path TO reg,pg_catalog,public;
-- ddl-end --

-- REVOKECONNECT ON DATABASE mosip_reg FROM PUBLIC;
-- REVOKEALL ON SCHEMA reg FROM PUBLIC;
-- REVOKEALL ON ALL TABLES IN SCHEMA reg FROM PUBLIC ;
