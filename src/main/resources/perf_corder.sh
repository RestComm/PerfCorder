ant -DJAVA_PID=$1 start
$2
ant -DJAVA_PID=$1 analyzeAndStop