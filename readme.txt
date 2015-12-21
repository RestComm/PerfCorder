How to run:

1 - Compile with 'mvn install'

2 - Copy the file 'target/sipp-report-0.0.1-SNAPSHOT-with-dependencies.jar' to the location of the statistics files produced by SIPP

3 - Run with 'java -jar' and check the available options with '-h' flag

4 - Have fun

How to make SIPP produce statistics files

1 - When starting SIPP use the following flags '-fd 1 -trace_stat'

2 - This will produce a .csv file that can be used by this tool

How to run collect/analysis tool:
1 - Install prerequesites (sysstat, jmvtop, gnuplot)
2 - Copy resources folder to where your sipp script is located
3 - Modify perfTools.properties ans set (Java PID, sipp script filename, folder where java conf is saved...).
4 - Run 'ant start'
5 - Run your sipp script
6 - When the test is finished, Run 'ant analyzeAndStop'
7 - All collected data can be found at target/..., a compressed file has been created.

