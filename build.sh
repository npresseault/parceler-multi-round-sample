#!/usr/bin/env bash

set -e

./gradlew install -p processor
./gradlew clean assembleDebug -p app -s --no-daemon --stacktrace --info