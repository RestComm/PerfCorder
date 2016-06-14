 #!/bin/bash
function HELP {
  echo -e \\n"Help documentation for ${SCRIPT}"\\n
  echo -e "${REV}Basic usage:${NORM} ${SCRIPT}"\\n
  echo "Command line switches are optional. The following switches are recognized."
  echo "-o  Output directory. Default is ./target."
  echo "-l  Path to log directory to be saved. Default is empty."
  echo "-d  Dump heap memory. Default is empty."
  echo "-f  Force GC. Default is empty."
  echo "-c  Package to filter class histogram. Default is empty."
  echo -e "-h --Displays this help message. No further functions are performed."\\n
  echo -e "Example: $SCRIPT "\\n
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
    if [[ -z $PC_SERVER_LOG_DIR ]]; then
        echo Saving server log disabled
    else
        echo Saving server log dir $PC_SERVER_LOG_DIR
        cp -a ${PC_SERVER_LOG_DIR} ${CONF_COLLECTION_DIR}
    fi  
}

function saveHeapDump {
    if [[ -z $PC_HEAP_DUMP_ENABLED ]]; then
        echo Saving heap dump disabled
    else
        echo Saving heap dump for ${JAVA_PID}
        jmap  -dump:file=${JAVA_COLLECTION_DIR}/heap.bin ${JAVA_PID} 2>> ${DATA_COLLECTION_DIR}/heapdump.out
    fi  
}

function forceGC {
    if [[ -z $PC_FORCE_GC ]]; then
        echo forceGC disabled
    else
        echo Forcing GC on ${JAVA_PID}
        jcmd $JAVA_PID GC.run
    fi  
}

function saveObjectHistogram {
    if [[ -z $PC_DUMP_OBJ_PACKAGE ]]; then
        echo Dump Objects disabled
    else
        echo Dumping Object on ${JAVA_PID}
        jcmd $JAVA_PID GC.class_histogram | grep $PC_DUMP_OBJ_PACKAGE > $JAVA_COLLECTION_DIR/objs.hist
    fi  
}

function stopCollection {
    echo Stopping collection on process $JAVA_PID
    killBackgroundProcesses
    forceGC
    saveServerLogs
    saveHeapDump
    saveObjectHistogram

    endTimestamp=$(date +%s)
    echo $endTimestamp > ${META_COLLECTION_DIR}/endTimestamp
    cd ${OUTPUT_DIR}
    zip -r ../perfTest-${endTimestamp}.zip data
}

#Set Script Name variable
SCRIPT=`basename ${BASH_SOURCE[0]}`
PC_FORCE_GC=
PC_DUMP_OBJ_PACKAGE=
OUTPUT_DIR=./target
EXTERNAL_FILES=
PC_HEAP_DUMP_ENABLED=
PC_SERVER_LOG_DIR=

while getopts "dfc:l:o:h" opt; do
  case $opt in
    o)
      OUTPUT_DIR=${OPTARG}
      ;;
    f)
      PC_FORCE_GC=true
      ;;
    c)
      PC_DUMP_OBJ_PACKAGE=${OPTARG}
      ;;
    d)
      PC_HEAP_DUMP_ENABLED=true
      ;;
    l)
      PC_SERVER_LOG_DIR=${OPTARG}
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

JAVA_PID=`cat ${META_COLLECTION_DIR}/java.pid`

stopCollection