@echo off

%~d0
cd %~p0

REM Java‚ÌŠÂ‹«•Ï”
if NOT {%JAVA_HOME_7%}=="" (
     SET JAVA_HOME=%JAVA_HOME_7%
)

rem ƒTƒCƒg¶¬
call mvn site:site

pause
