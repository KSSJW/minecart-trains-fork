#!/bin/bash

# gradle
rm -rf .gradle/
rm -rf build/
rm -rf out/
rm -rf classes/

# eclipse
rm *.launch

# idea
rm -rf .idea/
rm *.iml
rm *.ipr
rm *.iws

# vscode
rm -rf .settings/
rm -rf .vscode/
rm -rf bin/
rm .classpath
rm .project

# macos
rm *.DS_Store

# fabric
rm -rf run/

# java
rm hs_err_*.log
rm replay_*.log
rm *.hprof
rm *.jfr