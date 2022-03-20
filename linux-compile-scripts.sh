#!/bin/bash
./buildconfig/apache-ant-1.10.5/bin/ant -f buildconfig/build.xml compile-scripts
if [[ -d ./Scripts/misc ]]
then
    echo "Moving class files from ./Scripts/misc directory ./Scripts directory"
	mv ./Scripts/misc/*.class ./Scripts
fi
read -n 1 -s -r -p "Press any key to continue"