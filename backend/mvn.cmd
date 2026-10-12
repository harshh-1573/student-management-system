@echo off

REM Load environment variables from .env if present
if exist "%~dp0.env" (
    for /f "usebackq eol=# tokens=1* delims==" %%A in ("%~dp0.env") do (
        if not "%%A"=="" (
            set "%%A=%%B"
        )
    )
)

REM Ensure JWT_SECRET is set if not already defined
if "%JWT_SECRET%"=="" (
    set "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
)

call "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" %*
