@echo off
REM prepare-biosdk-mock-lib.bat
REM
REM Downloads the BioSDK mock JAR from Maven Central and packages it as mock-sdk.zip
REM inside the WireMock __files directory so that biosdk-service can fetch it on startup.
REM
REM Run this script ONCE before the first "docker compose up".
REM Re-run only if you need to refresh the file.
REM
REM Requirements: curl (built-in Windows 10+), PowerShell 5.0+ (for Compress-Archive)

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set TARGET_DIR=%SCRIPT_DIR%docker-compose\wiremock\__files
set JAR_URL=https://repo1.maven.org/maven2/io/mosip/mock/sdk/mock-sdk/1.3.0-beta.1/mock-sdk-1.3.0-beta.1-jar-with-dependencies.jar
set JAR_NAME=mock-sdk-1.3.0-beta.1-jar-with-dependencies.jar
set ZIP_NAME=mock-sdk.zip
set TEMP_DIR=%TEMP%\biosdk-prepare-%RANDOM%

echo ========================================
echo  BioSDK Mock Library Preparation
echo ========================================
echo.

REM Create temp directory
mkdir "%TEMP_DIR%" 2>nul

echo [1/3] Downloading mock-sdk JAR from Maven Central (~110 MB)...
curl -L --progress-bar "%JAR_URL%" -o "%TEMP_DIR%\%JAR_NAME%"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Download failed. Check your internet connection and retry.
    rmdir /s /q "%TEMP_DIR%"
    exit /b 1
)
echo       Download complete.
echo.

echo [2/3] Packaging as %ZIP_NAME%...
powershell -NoProfile -Command "Compress-Archive -Path '%TEMP_DIR%\%JAR_NAME%' -DestinationPath '%TEMP_DIR%\%ZIP_NAME%' -Force"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to create zip archive.
    echo        Ensure PowerShell 5.0 or later is available on this machine.
    rmdir /s /q "%TEMP_DIR%"
    exit /b 1
)
echo       Zip created.
echo.

echo [3/3] Copying to WireMock __files directory...
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
copy "%TEMP_DIR%\%ZIP_NAME%" "%TARGET_DIR%\%ZIP_NAME%" >nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to copy zip to target directory.
    rmdir /s /q "%TEMP_DIR%"
    exit /b 1
)
echo       Copied to: %TARGET_DIR%\%ZIP_NAME%
echo.

echo Cleaning up temporary files...
rmdir /s /q "%TEMP_DIR%"

echo ========================================
echo  Done! biosdk-service is ready to start.
echo ========================================
echo.
echo Next step:
echo   cd local-dev-setup\docker-compose
echo   docker compose up -d
