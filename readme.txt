How to run:

1 - Compile with 'mvn install'

2 - Copy the file 'target/sipp-report-0.0.1-SNAPSHOT-with-dependencies.jar' to the location of the statistics files produced by SIPP

3 - Run with 'java -jar' and check the available options with '-h' flag

4 - Have fun

How to make SIPP produce statistics files

1 - When starting SIPP use the following flags '-fd 1 -trace_stat'

2 - This will produce a .csv file that can be used by this tool

How to run collect tool:
1 - Install prerequesites (sysstat, jmvtop, gnuplot)
2 - Copy resources folder to where your sipp script are located
3 - Modify perfTools.properties to point to the java process(PID) under monitoring
4 - Run 'ant -f collect-build.xml start'
5 - Run your sipp script
6 - When the test is finished, Run 'ant -f collect-build.xml stop'
7 - All collected data can be found at target/data..., a compressed file has been created.

How to run analyze tool:
1 - Assuming you have run a collection previouly
2 - Run 'ant -f analyze-build.xml"
3 - Graphs and stats has been created aunder target/analysis

