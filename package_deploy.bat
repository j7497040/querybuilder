@echo off

%~d0
cd %~p0

REM Java�̊��ϐ�
if NOT {%JAVA_HOME_7%}=="" (
    SET JAVA_HOME=%JAVA_HOME_7%
)
rem �R���p�C��
call mvn compile

rem jar�f�v���C
call mvn -Dmaven.test.skip=true source:jar deploy

pause
