 #!/bin/bash
function HELP {
  echo -e \\n"Help documentation for ${SCRIPT}."\\n
  echo -e "Basic usage:$SCRIPT file.ext"\\n
  echo "Command line switches are optional. The following switches are recognized."
  echo "-f  Frequency in seconds. Default is 4."
  echo "-o  Output directory. Default is ./target."
  echo "-j  Path to join file where sorted classes are listed"
  echo "-r  Rotate mode, specify seconds for snapshot creation. By default is disabled/-1"
  echo "-d  minutes to hold snapshots before removal. Default is 1440"
  echo "-m path to jmx2csv xml metafile"
  echo "-t thread prefix filter"

  echo -e "-h  --Displays this help message. No further functions are performed."\\n
  echo -e "Example: $SCRIPT -f 1 -c /opt/conf jmx_url"\\n
  exit 1
}

function killBackgroundProcesses {
    echo Kill previous processes
    for f in ${DATA_COLLECTION_DIR}/*.pid; do
        echo PID file to kill:$f
        cat $f | xargs kill
    done
}

function cleanCollection {
    echo Preparing output dirs
    rm -Rf ${DATA_COLLECTION_DIR}
}

function prepareCollectionOutputDirs {
    echo Preparing output dirs
    mkdir -p ${META_COLLECTION_DIR}
    mkdir -p ${JAVA_COLLECTION_DIR}
}

function collectJavaProcessInfo {
    echo Collect Java Process Info

    $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.VMInfoPrinter ${JAVA_PID} > ${META_COLLECTION_DIR}/jvmdump.txt

    echo $JAVA_PID > ${META_COLLECTION_DIR}/java.pid

    curl http://169.254.169.254/latest/dynamic/instance-identity/document > ${META_COLLECTION_DIR}/ec2instance.doc
}

function startJavaMeasCollection {
    echo Starting Java Collection over process ${JAVA_PID}

    $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.JVMStatApp -d ${MEAS_INTERVAL_SECONDS} ${JAVA_PID} > ${JAVA_COLLECTION_DIR}/jvmtop.txt &
    echo $! > ${DATA_COLLECTION_DIR}/jvmstat.pid

    $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.GCPausePrinter -d ${MEAS_INTERVAL_SECONDS} ${JAVA_PID} > ${JAVA_COLLECTION_DIR}/jgcstat.txt &
    echo $! > ${DATA_COLLECTION_DIR}/jgcstat.pid

    if [[ -z $JMX2CSV ]]; then
        echo "JMX2CSV disabled"
    else
        echo "JMX2CSV enabled"
        $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.JMX2CSVApp -d ${MEAS_INTERVAL_SECONDS} -m ${JMX2CSV} > ${JAVA_COLLECTION_DIR}/jmx2csv.csv &
        echo $! > ${DATA_COLLECTION_DIR}/jmx2csv.pid
    fi


    $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.ThreadStatApp -d ${MEAS_INTERVAL_SECONDS} ${JAVA_PID} > ${JAVA_COLLECTION_DIR}/threads.csv &
    echo $! > ${DATA_COLLECTION_DIR}/threads.pid
    if [[ -z ${THREAD_PREFIX_FILTER} ]]; then
        echo Thread prefix disabled
    else 
        echo Thread prefix enabled
        $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH org.restcomm.perfcorder.collector.ThreadStatApp -d ${MEAS_INTERVAL_SECONDS} -f ${THREAD_PREFIX_FILTER} ${JAVA_PID} > ${JAVA_COLLECTION_DIR}/prefixThreads.csv &
        echo $! > ${DATA_COLLECTION_DIR}/prefixThreads.pid
    fi

}

function invokeExternalHook {
    if [[ -z $INVOKE_EXTERNAL_HOOK ]]; then
        echo "Invoke External Hook Disabled"
    else
        echo "Invoke External Hook at:$INVOKE_EXTERNAL_HOOK"
        bash $INVOKE_EXTERNAL_HOOK
    fi  
}

function startCollection {
    echo Starting collection
    killBackgroundProcesses
    cleanCollection
    prepareCollectionOutputDirs
    printCollectionSettings
    collectConfUsed
    collectJavaProcessInfo

    #invoke external before starting actual collection
    invokeExternalHook

    startTimestamp=$(date +%s)
    echo $startTimestamp > ${META_COLLECTION_DIR}/startTimestamp

    startJavaMeasCollection
}

function waitForPID {
    echo "Pattern mode activated, try to find PID with pattern:$JPS_PATTERN"
    LOOP_COUNT=0
    ##lets wait 20 times the configured MEAS_INTERVAL maximum
    while [[ -z ${JAVA_PID} ]] && [ "$LOOP_COUNT" -lt 20 ]
    do
        sleep $MEAS_INTERVAL_SECONDS
        export JAVA_PID=$(jps -l |grep $JPS_PATTERN | cut -d' ' -f1)
        echo "PID found do far:$JAVA_PID"
        LOOP_COUNT=$((LOOP_COUNT+1))
    done
    if [[ -z ${JAVA_PID} ]]; then
        echo "No process found,exiting"
        exit 2
    fi
}

function printCollectionSettings {
    echo "Printing PerfCorder settings"
    echo "MEAS_INTERVAL_SECONDS,CONF_DIR,OUTPUT_DIR,PATTERN_MODE,JAVA_PID" > ${META_COLLECTION_DIR}/perfcorder_settings.csv
    echo "$MEAS_INTERVAL_SECONDS,$CONF_DIR,$OUTPUT_DIR,$PATTERN_MODE,$JAVA_PID" >> ${META_COLLECTION_DIR}/perfcorder_settings.csv
}

function takeSnapshot {
    endTimestamp=$(date +%s)
    echo $endTimestamp > ${META_COLLECTION_DIR}/endTimestamp
    echo "Taking snapshot $endTimestamp"
    zip -r ../perfTest-${startTimestamp}-${endTimestamp}.zip data

    echo "Discarding old snapshot"
    find /path/to/your/dir/tree -atime +XXX -exec rm {}\;
 


    echo "Resuming Collection"
    startTimestamp=$(date +%s)
    echo $startTimestamp > ${META_COLLECTION_DIR}/startTimestamp
}

#Set Script Name variable
SCRIPT=`basename ${BASH_SOURCE[0]}`

export OUTPUT_DIR=./target
export MEAS_INTERVAL_SECONDS=4
export CONF_DIR=
PATTERN_MODE=disabled
PC_NETWORK_CAPTURE=""
ROTATE_MODE=-1
SNAPSHOT_RETENTION_MIN=1440
JMX2CSV=""
THREAD_PREFIX_FILTER=""
REMOTE_URL=""

#Check the number of arguments. If none are passed, print help and exit.
NUMARGS=$#
echo -e \\n"Number of arguments: $NUMARGS"
if [ $NUMARGS -eq 0 ]; then
  HELP
fi

while getopts "f:c:o:e:j:r:d:m:t:ph" opt; do
  case $opt in
    f)
      MEAS_INTERVAL_SECONDS=${OPTARG}
      ;;
    c)
      CONF_DIR=${OPTARG}
      ;;
    o)
      OUTPUT_DIR=${OPTARG}
      ;;
    e)
      INVOKE_EXTERNAL_HOOK=${OPTARG}
      ;;
    j)
      JOIN_FILE_PATH=${OPTARG}
      ;;
    r)
      ROTATE_MODE=${OPTARG}
      ;;
    d)
      SNAPSHOT_RETENTION_MIN=${OPTARG}
      ;;
    m)
      JMX2CSV=${OPTARG}
      ;;
    t)
      THREAD_PREFIX_FILTER=${OPTARG}
      ;;
    n)
      PC_NETWORK_CAPTURE=ENABLED
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

echo "Process monitored $JAVA_PID"

TOOLSJAR="$JAVA_HOME/lib/tools.jar"
if [ ! -f "$TOOLSJAR" ] ; then
        echo "$JAVA_HOME seems to be no JDK!" >&2
        exit 1
fi

if [[ -z ${PERFCORDER_HOME} ]]; then
    DIR=$( cd $(dirname $0) ; pwd -P )
else 
    DIR=$PERFCORDER_HOME
fi

#maven filtering has to replace the version var during build
CLASSPATH="$DIR/sipp-report-with-dependencies.jar:$TOOLSJAR:$PERF_ADD_LIBS"
echo CLASSPATH:${CLASSPATH}

DATA_COLLECTION_DIR=${OUTPUT_DIR}/data
META_COLLECTION_DIR=${DATA_COLLECTION_DIR}/meta
CONF_COLLECTION_DIR=${DATA_COLLECTION_DIR}/conf
PERIODIC_COLLECTION_DIR=${DATA_COLLECTION_DIR}/periodic
JAVA_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/java
SYS_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/sys
SIP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/sip
HTTP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/http
SMPP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/smpp
MAP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/map
TCAP_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/tcap
DIAMETER_COLLECTION_DIR=${PERIODIC_COLLECTION_DIR}/diameter
    
ANALYSIS_GENERATION_DIR=${OUTPUT_DIR}/analysis
GRAPHS_DIR=${ANALYSIS_GENERATION_DIR}/graphs
STATS_DIR=${ANALYSIS_GENERATION_DIR}/stats

startCollection
