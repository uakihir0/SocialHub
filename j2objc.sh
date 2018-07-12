#!/usr/bin/env bash

./gradlew -b j2objc.gradle clean :j2objcAssemble -x test
sh ./tool/buildscript.sh