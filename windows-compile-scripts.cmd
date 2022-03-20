@echo off
SET antpath="buildconfig\apache-ant-1.10.5\bin\"

call %antpath%ant -f buildconfig\build.xml compile-scripts

if exist Scripts\misc (
  echo "Moving class files from ./Scripts/misc directory ./Scripts directory"
  XCOPY /S Scripts\misc\*.class Scripts\*.class
)
pause