-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_kernel
-- Release Version 	: 0.10.4
-- Purpose    		: Revoking Database Alter deployement done for release in Kernel DB.       
-- Create By   		: Sadanandegowda
-- Created Date		: 28-Nov-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------

\c mosip_kernel sysadmin

--------- VID POOL REQUIREMENT DB CHANGES REVOKE -----------

DROP TABLE IF EXISTS kernel.vid;