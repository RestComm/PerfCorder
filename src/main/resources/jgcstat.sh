#!/bin/sh
DIR=$( cd $(dirname $0) ; pwd -P )

if [ -z "$JAVA_HOME" ] ; then
        JAVA_HOME=`readlink -f \`which java 2>/dev/null\` 2>/dev/null | \
        sed 's/\/bin\/java//'`
fi

TOOLSJAR="$JAVA_HOME/lib/tools.jar"

if [ ! -f "$TOOLSJAR" ] ; then
        echo "$JAVA_HOME seems to be no JDK!" >&2
        exit 1
fi

"$JAVA_HOME"/bin/java $JAVA_OPTS -cp "$DIR/sipp-report-0.2-SNAPSHOT-with-dependencies.jar:$TOOLSJAR" \
org.restcomm.perfcorder.collector.GCPausePrinter "$@"
exit $?