 #!/bin/bash
function HELP {
  echo -e \\n"Help documentation for ${BOLD}${SCRIPT}.${NORM}"\\n
  echo -e "${REV}Basic usage:${NORM} ${BOLD}$SCRIPT file.ext${NORM}"\\n
  echo "Command line switches are optional. The following switches are recognized."
  echo "${REV}-o${NORM}  Output directory. Default is ${BOLD}./target${NORM}."
  echo -e "${REV}-h${NORM}  --Displays this help message. No further functions are performed."\\n
  echo -e "Example: ${BOLD}$SCRIPT ${NORM}"\\n
  exit 1
}

function killBackgroundProcesses {
    echo Kill previous processes
    for f in ${DATA_COLLECTION_DIR}/*.pid; do
        echo PID file to kill:$f
        cat $f | xargs kill
    done
}
function saveServerLogs {
    if [[ -z $SERVER_LOG_DIR ]]; then
        echo Saving server log disabled
    else
        echo Saving server log dir $SERVER_LOG_DIR
        cp -a ${SERVER_LOG_DIR} ${CONF_COLLECTION_DIR}
    fi  
}

function saveHeapDump {
    if [[ -z $HEAP_DUMP_ENABLED ]]; then
        echo Saving heap dump disabled
    else
        echo Saving heap dump for ${JAVA_PID}
        jmap  -dump:file=${JAVA_COLLECTION_DIR}/heap.bin ${JAVA_PID} 2>> ${DATA_COLLECTION_DIR}/heapdump.out
    fi  
}

function stopCollection {
    echo Stopping collection
    killBackgroundProcesses
    saveServerLogs
    saveHeapDump

    endTimestamp=$(date +%s)
    echo $endTimestamp > ${META_COLLECTION_DIR}/endTimestamp
    zip -r ./perfTest-${endTimestamp}.zip ${OUTPUT_DIR}
}

#Set Script Name variable
SCRIPT=`basename ${BASH_SOURCE[0]}`

OUTPUT_DIR=./target
EXTERNAL_FILES=

while getopts "o:h" opt; do
  case $opt in
    o)
      OUTPUT_DIR=${OPTARG}
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

DATA_COLLECTION_DIR=${OUTPUT_DIR}/data
META_COLLECTION_DIR=${DATA_COLLECTION_DIR}/meta
CONF_COLLECTION_DIR=${DATA_COLLECTION_DIR}/conf
PERIODIC_COLLECTION_DIR=${DATA_COLLECTION_DIR}/periodic
JAVA_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/java
SYS_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/sys
SIP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/sip
    
ANALYSIS_GENERATION_DIR=${OUTPUT_DIR}/analysis
GRAPHS_DIR=${ANALYSIS_GENERATION_DIR}/graphs
STATS_DIR=${ANALYSIS_GENERATION_DIR}/stats

stopCollection