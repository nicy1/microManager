#!/bin/bash
# A simple script for running program

echo "Starting program"

#cd /home/ubuntu/microManager
#in the end remove the following line
mvn verify
mvn exec:java -Dexec.mainClass=main.Main
