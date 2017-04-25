#!/bin/sh
if [ -z ${PERFCORDER_HOME} ]; then
    DIR=$( cd $(dirname $0) ; pwd -P )
else 
    echo "Using PERFCORDER_HOME at:$PERFCORDER_HOME"
    DIR=$PERFCORDER_HOME
fi

if [ -z "$JAVA_HOME" ] ; then
        JAVA_HOME=`readlink -f \`which java 2>/dev/null\` 2>/dev/null | \
        sed 's/\/bin\/java//'`
fi

TOOLSJAR="$JAVA_HOME/lib/tools.jar"

if [ ! -f "$TOOLSJAR" ] ; then
        echo "$JAVA_HOME seems to be no JDK!" >&2
        exit 1
fi

"$JAVA_HOME"/bin/java $JAVA_OPTS -cp "$DIR/sipp-report-with-dependencies.jar:$TOOLSJAR" \
org.restcomm.perfcorder.collector.GCPausePrinter "$@" &
echo $! > jgcstat.pid
exit $?