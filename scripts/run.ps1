#!/usr/bin/env zsh

Push-Location $PSScriptRoot\..
.\build.ps1
java -jar ..\build\libs\vpn-manager-1.3.5-uber.jar
Pop-Location
