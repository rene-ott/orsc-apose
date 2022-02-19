#!/bin/bash
./buildconfig/apache-ant-1.10.5/bin/ant -f buildconfig/build.xml compile-bot
read -n 1 -s -r -p "Press any key to continue"