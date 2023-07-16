#!/usr/bin/env zsh

Push-Location $PSScriptRoot
./build.sh
java -jar build/libs/vpn-manager-1.3.4-uber.jar
Pop-Location
