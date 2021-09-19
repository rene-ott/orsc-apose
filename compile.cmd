@echo off
SET antpath="apache-ant-1.10.5\bin\"

call %antpath%ant -f build.xml compile
pause