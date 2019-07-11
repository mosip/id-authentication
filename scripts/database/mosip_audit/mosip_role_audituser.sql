-- object: audituser | type: ROLE --
-- DROP ROLE IF EXISTS audituser;
CREATE ROLE audituser WITH 
	INHERIT
	LOGIN
	PASSWORD 'Mosip@dev123';
-- ddl-end --
