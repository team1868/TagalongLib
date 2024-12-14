#!/bin/bash

# Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
# Open Source Software; you may modify and/or share it under the terms of
# the 3-Clause BSD License found in the root directory of this project.

set -x
echo "$@" | xargs -L1 sed -i '' -E \
    "s~Copyright *\([0-9,-]+\)*-([0-9]{4}) *The *Space *Cookies *: *Girl *Scout *Troop *#62868 *and *FRC *Team *#1868~Copyright \\1-`date +%Y` The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868~; /`date +%Y`/! s~Copyright *([0-9]{4}) *The *Space *Cookies *: *Girl *Scout *Troop *#62868 *and *FRC *Team *#1868~Copyright \\1-`date +%Y` The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868~g"
