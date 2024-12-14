#!/bin/bash

# Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
# Open Source Software; you may modify and/or share it under the terms of
# the 3-Clause BSD License found in the root directory of this project.

./gradlew build
git submodule update --init --recursive

# Checkout a new branch with the same name as the current branch from main in the docs submodule
name=`git rev-parse --abbrev-ref HEAD`
cd TagalongLibDocs
git fetch
git checkout -b $name

# Replace docs with newly built docs
rm -rf docs
mv ../build/docs/javadoc docs
git add docs
# Commit
message="Update docs from TagalongLib branch $name"
git commit -m "$message"
git push -u origin $name
cd ..
