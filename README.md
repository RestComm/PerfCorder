#PerfCorder
============

[![Join the chat at https://gitter.im/RestComm/PerfCorder](https://badges.gitter.im/RestComm/PerfCorder.svg)](https://gitter.im/RestComm/PerfCorder?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

PerfCorder allows you to record and analyse data during performance testing using Sipp tool.
It is specifically oriented to monitor a Java process that is processing the SIP signalling.

The tool is based on Bash scripts, that allows you to start and stop the recording whenever you like.

The recording will include performance information about the system (io, cpu, VM, net) and the java process (GC....).

Since the environment you are using to do the performance testing is as important as the performance data, several meta data is also saved (java opts,start/end timestamp..).

Analyse target will create stats and graphs about the recorded data to help you interpreting the performance test results.

Finally the tool will package all the collected and analysed data into a single zip file.

The tool encourages a default directory layout following Maven convention over configuration principles.
This is the current dir layout:

target

    --->data

        --->meta (decicated to save information about the performance test itself)

            ---><start/endTime>

            ---><copyOfsippScript>

            ---><jar files mounted by java process>

            ---><JVM options used to start the java process>

        --->conf (save files that change java process behavior)

        --->periodic (save perf data every N seconds)

            --->sys

                --->iostat/vmstat/netstat

            --->java
                ---> GCCollection

                ---> jstat

                ---> CPU/Mem/GC CPU
            --->sip

                ---> sipp_stat.csv

                ---> sipp_rtt.csv


#Documentation
========
##How to run SIPP Report tool:

1. Compile with 'mvn install'
2. Copy the file 'target/sipp-report-0.0.1-SNAPSHOT-with-dependencies.jar' to the location of the statistics files produced by SIPP
3. Run with 'java -jar' and check the available options with '-h' flag
4. Have fun

##How to make SIPP produce statistics files

1. When starting SIPP use the following flags '-fd 1 -trace_stat'
2. This will produce a .csv file that can be used by this tool

##How to run collect tool:

1. Install prerequesites (sysstat, java JDK)
2. Create a directory to contain PerfCorder
3. Download precompiled PerfCorder binaries from (both jar files) [PerfCorder CI](https://mobicents.ci.cloudbees.com/job/PerfCorder/lastSuccessfulBuild/artifact/target/) into PerfCorder dir
4. Extract contents of small jar into PerfCorder dir, so you get access to CLI tools.
5. Run 'pc_start_collect.sh <java_pid>', from the download directory ("-h" for more options)
6. Run your sipp script
7. When the test is finished, Run 'pc_stop_collect.sh' ("-h" for more options)

See command help
```
[root@avaya perfcorder]# ./pc_start_collect.sh -h

Number of arguments: 0

Help documentation for pc_start_collect.sh.

Basic usage:pc_start_collect.sh file.ext

Command line switches are optional. The following switches are recognized.
-f  Frequency in seconds. Default is 4.
-o  Output directory. Default is ./target.
-c  Copy this path into conf dir. Default is empty.
-j  Path to join file where sorted classes are listed
-p  Pattern mode. PID is a grep pattern applied to jps output, to find actual PID.
-r  Rotate mode, specify seconds for snapshot creation. By default is disabled/-1
-d  minutes to hold snapshots before removal. Default is 1440
-n enable network capture using tshark
-h  --Displays this help message. No further functions are performed.


Example: pc_start_collect.sh -f 1 -c /opt/conf java_pid


[root@avaya perfcorder]# ./pc_stop_collect.sh -h

Help documentation for pc_stop_collect.sh

Basic usage: pc_stop_collect.sh

Command line switches are optional. The following switches are recognized.
-o  Output directory. Default is ./target.
-l  Path to log directory to be saved. Default is empty.
-d  Dump heap memory. Default is empty.
-f  Force GC. Default is empty.
-c  Package to filter class histogram. Default is empty.
-h --Displays this help message. No further functions are performed.

Example: pc_stop_collect.sh

```

##How to run analysis tool:

1. Run "`pc_analyse.sh <zipfile> <percentageToStripFromCSVs>`"
2. An XML file with all the stats will be printed to std out

The format of this XML file is:
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<perfCorderAnalysis>
    <startTimeStamp>1460927104000</startTimeStamp>
    <endTimeStamp>1460928976000</endTimeStamp>
    <measMap>
        <entry>
            <key>SIPResponseTime</key>
            <value>
                <count>180000.0</count>
                <geometricMean>1.0</geometricMean>
                <graph></graph>
                <kurtosis>NaN</kurtosis>
                <max>1.0</max>
                <mean>1.0</mean>
                <median>1.0</median>
                <min>1.0</min>
                <percentile25>1.0</percentile25>
                <percentile5>1.0</percentile5>
                <percentile75>1.0</percentile75>
                <percentile95>1.0</percentile95>
                <quadraticMean>1.0</quadraticMean>
                <skewness>NaN</skewness>
                <stdDev>0.0</stdDev>
                <sum>180000.0</sum>
                <sumSquares>180000.0</sumSquares>
                <variance>0.0</variance>
            </value>
        </entry>
</measMap>
</perfCorderAnalysis>        
```

##How to run test tool:

1. Run "`cat <analysis_xml_file> | pc_test.sh <goals_xsl_file>`"
2. A JUnit XML report file is printed in standard output with test results.

Since the output of the analysis phase is an XML file, your performance goals at
 testing level has to be expressed through an XSL transformation stylesheet. Your
 stylesheet needs to include the base one <xsl:include href="junitTestCaseTemplate.xsl"/>
so basic functionality is provided ("biggerThanTemplate" and "lessThanTemplate"). The base stylesheet will be included through classloader when invoking the test tool.
Find an example below:
```
<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="false" omit-xml-declaration="yes"/>
	<xsl:include href="junitTestCaseTemplate.xsl"/>

	<xsl:template match="/" priority="9">
		<testsuite>
			<xsl:for-each select="//key[text()='Cpu']/parent::entry/value/median">
				<xsl:call-template name="lessThanTemplate">
					<xsl:with-param name="caseName" select="'CPUMedian'" />
					<xsl:with-param name="thresholdValue"  select="'67'" />   
				</xsl:call-template>
			</xsl:for-each>
		</testsuite>   
	</xsl:template>
</xsl:stylesheet>
```
In this case, a measurement called "Cpu" will be searched on the input XML analysis file.
And the "median" of that measurement will be used to create a less than test case. The test case
will be named "CPUMedian" in the output JUnit report file. And the value "67" will be used to
set the less than threshold.

As you can see, this simple yet powerful means of declaring your performance goals, allows
 you to create endless possibilities for your performance goals. Of course, you are
the one making sense of it all. For example, a CPU measurement may be more focused towards mean/median stats, while a ResponseTime measurement goal may be centered around high percentiles like percentile95 stat.

##How to generate HTML view
1. Run "`cat <analysis_xml_file> | pc_html_gen.sh`"
2. A JUnit XML report file is printed in standard output with test results.

#Coming Soon
========
- [ ] Diff script to compare different collection files.
- [ ] HTML summary view generation from analysis file.
- [ ] Publish REST API for cloud deployment.

#Want to Contribute ?
========
[See our Contributors Guide](https://github.com/Mobicents/sip-servlets/wiki/Contribute-to-Mobicents-SIP-Servlets)

#Issue Tracking and Roadmap
========
[Issue Tracker](https://github.com/Mobicents/PerfCorder/issues)

#Questions ?
========
Please ask your question on [StackOverflow](http://stackoverflow.com/search?q=mobicents) or the Google [public forum](http://groups.google.com/group/mobicents-public)

#License
========

PerfCorder is lead by [TeleStax](http://www.telestax.com/), Inc. and developed collaboratively by a community of individual and enterprise contributors.

PerfCorder is licensed under dual license policy. The default license is the Free Open Source GNU Affero General Public License v3.0. Alternatively a commercial license can be obtained from Telestax ([contact form](http://www.telestax.com/contactus/#InquiryForm))

#Continuous Integration and Delivery
========
[PerfCorder CI](https://mobicents.ci.cloudbees.com/job/PerfCorder/lastSuccessfulBuild/artifact/target/)

#Acknowledgements
========
[See who has been contributing to RestComm](http://www.telestax.com/opensource/acknowledgments/)
