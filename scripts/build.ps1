#!/usr/bin/env zsh

Push-Location $PSScriptRoot
.\gradlew clean uberJar
Pop-Location
