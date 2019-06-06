@echo OFF

set d=%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%
set t=%time::=.% 
set t=%t: =%
set logfile="%d%_%t%.log"

taskkill /FI "WINDOWTITLE eq select MOSIP_MDM_SERVICE"
taskkill /FI "WINDOWTITLE eq MOSIP_MDM_SERVICE"
pause
