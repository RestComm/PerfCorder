


[Try Restcomm Cloud NOW for FREE!](https://www.restcomm.com/sign-up/) Zero download and install required.


All Restcomm [docs](https://www.restcomm.com/docs/) and [downloads](https://www.restcomm.com/downloads/) are now available at [Restcomm.com](https://www.restcomm.com).



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

