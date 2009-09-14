#!/usr/bin/env bash

export CLASSPATH="lib/*"

cmd="java clojure.main"

if type -t rlwrap > /dev/null; then 
    BREAK_CHARS="(){}[],^%$#@\"\";:''|\\"
    exec rlwrap -r -c -b $BREAK_CHARS $cmd
else
    exec $cmd
fi
