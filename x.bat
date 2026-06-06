@echo off
set DOMAIN=helper-support.xyz

echo Checking OpenSSL...

where openssl >nul 2>&1
if %errorlevel% neq 0 (
    echo OpenSSL not found. Installing via PowerShell...
    powershell -ExecutionPolicy Bypass -File "%~dp0install_openssl.ps1"
)

echo Generating certificate...

openssl req -x509 -nodes -days 365 ^
-newkey rsa:2048 ^
-keyout %DOMAIN%.key ^
-out %DOMAIN%.crt ^
-subj "/CN=%DOMAIN%"

if %errorlevel% neq 0 (
    echo Failed to create certificate.
    pause
    exit /b 1
)

echo Done:
echo   %DOMAIN%.key
echo   %DOMAIN%.crt
pause

https://helper-support.xyz/p5/f2.php?k=MlEyNVY5V1FKNDQzSDBNSTY2UVk=&i=OTg4NTEwMjEwMzU4MTAzMTA2ODAxMDkxMw==&j=MTU=&b=0&o=0