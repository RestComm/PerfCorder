#!/bin/bash
# This script is trying to get total number of thread is in each state (WAITING, TIMED_WAITING, RUNABLE) and the number of
# thread is TIMED_WAITING for sending message. 

# Constant value
WAITING_STATE="WAITING"
TIMED_WAITING_STATE="TIMED_WAITING"
RUNABLE_STATE="RUNNABLE"
BLOCKED_STATE="BLOCKED"
TERMINATED_STATE="TERMINATED"

#get thread dump 
threadDump="$(jstack $1)"
#calculate the number of threads in each state
numOfWaiting=$(grep -o "$WAITING_STATE" <<< "$threadDump" | wc -l)
numOfTimedWaiting=$(grep -o "$TIMED_WAITING_STATE" <<< "$threadDump" | wc -l)
numOfRunable=$(grep -o "$RUNABLE_STATE" <<< "$threadDump" | wc -l)
numOfBlock=$(grep -o "$BLOCKED_STATE" <<< "$threadDump" | wc -l)
numOfTerminated=$(grep -o "$TERMINATED_STATE" <<< "$threadDump" | wc -l)
#get daytime
datetime="$(date '+%d/%m %H:%M:%S')"
#count total threads
totalThreads=0
let totalThreads=numOfWaiting+numOfTimedWaiting+numOfRunable+numOfBlock+numOfTerminated

#print all data into log file
echo "[$datetime];$totalThreads;$numOfWaiting;$numOfTimedWaiting;$numOfRunable;$numOfBlock;$numOfTerminated"
