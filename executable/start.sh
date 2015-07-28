#!/bin/sh
java -classpath ".:resources:lib/*" eu.semagrow.recommender.Recommender > output.txt 2>&1 &