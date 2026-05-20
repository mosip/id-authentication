CREATE DATABASE :mosipdbname
	ENCODING = 'UTF8'
	LC_COLLATE = 'en_US.UTF-8'
	LC_CTYPE = 'en_US.UTF-8'
	TABLESPACE = pg_default
	OWNER = postgres
	TEMPLATE  = template0;
COMMENT ON DATABASE :mosipdbname IS 'ID Authorization related requests, transactions and mapping related data like virtual ids, tokens, etc. will be stored in this database';

\c :mosipdbname

DROP SCHEMA IF EXISTS ida CASCADE;
CREATE SCHEMA ida;
ALTER SCHEMA ida OWNER TO postgres;
ALTER DATABASE :mosipdbname SET search_path TO ida,pg_catalog,public;
