@echo off

%~d0
cd %~p0

REM Java�̊��ϐ�
if NOT {%JAVA_HOME_7%}=="" (
     SET JAVA_HOME=%JAVA_HOME_7%
)

rem �T�C�g����
call mvn site:site

pause
