#!/bin/sh
java -classpath ".:../lib/*" -Xss64M -Xmx2048M eu.semagrow.recommender.Recommender > output.txt 2>&1 &