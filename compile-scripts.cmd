@echo off
SET antpath="buildconfig\apache-ant-1.10.5\bin\"

call %antpath%ant -f buildconfig\build.xml compile-scripts
pause