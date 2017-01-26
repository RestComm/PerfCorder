#!/bin/bash
function HELP {
  echo -e \\n"Help documentation for ${SCRIPT}."\\n
  echo -e "Basic usage:$SCRIPT java_pid"\\n
  echo "Command line switches are optional. The following switches are recognized."
  echo "-f  Frequency in seconds. Default is 4."
  echo "-j  Path to join file where sorted classes are listed"
  echo -e "-h  --Displays this help message. No further functions are performed."\\n
  echo -e "Example: $SCRIPT -f 1 -j ./class.join java_pid"\\n
  exit 1
}

export MEAS_INTERVAL_SECONDS=4
export JOIN_FILE_PATH=./obj.join
if [ -z ${PERFCORDER_HOME} ]; then
    DIR=$( cd $(dirname $0) ; pwd -P )
else 
    echo "Using PERFCORDER_HOME at:$PERFCORDER_HOME"
    DIR=$PERFCORDER_HOME
fi

#Check the number of arguments. If none are passed, print help and exit.
NUMARGS=$#
if [ $NUMARGS -eq 0 ]; then
  HELP
fi

while getopts "f:j:" opt; do
  case $opt in
    f)
      MEAS_INTERVAL_SECONDS=${OPTARG}
      ;;
    j)
      JOIN_FILE_PATH=${OPTARG}
      ;;
    h)
      HELP
      ;;
    \?)
      HELP
      ;;
  esac
done
shift $((OPTIND-1))

# HANDLE CORRECT NUMBER OF MASS OPTIONS
if [ $# -ne 1 ]; then
  HELP
  exit 1
fi

export JAVA_PID=$1

#print csv file header
cat $JOIN_FILE_PATH | awk -f $PERFCORDER_HOME./transpose.awk

while :
do
    jcmd $JAVA_PID GC.class_histogram | awk '{print $4,$2}' | sort | join --nocheck-order $JOIN_FILE_PATH - | awk -f $PERFCORDER_HOME./transpose.awk | head -1
    sleep $MEAS_INTERVAL_SECONDS
done