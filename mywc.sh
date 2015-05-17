#!/bin/bash
if [ ! -f MyWC.jar ]; then
    ant build
fi
java -jar MyWC.jar $*
exit
