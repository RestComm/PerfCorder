/**
 * jvmtop - java monitoring for the command-line
 *
 * Copyright (C) 2013 by Patric Rufflar. All rights reserved. DO NOT ALTER OR
 * REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 only, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.restcomm.perfcorder.collector;

import java.lang.management.ThreadInfo;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

/**
 * "detail" view, printing detail metrics of a specific jvm in a vmstat manner.
 *
 *
 * @author
 *
 */
public class ThreadStatView extends AbstractConsoleView {

    private VMInfo vmInfo_;

    private String prefix;

    public ThreadStatView(int vmid, Integer width, String prefix) throws Exception {
        super(width);
        this.prefix = prefix;
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        vmInfo_ = VMInfo.processNewVM(localVirtualMachine, vmid);
    }

    public ThreadStatView(String url, Integer width, String prefix) throws Exception {
        super(width);
        this.prefix = prefix;
        ProxyClient proxyClient = ProxyClient.getProxyClient(url,
                "",
                "");
        proxyClient.connect();

        vmInfo_ = new VMInfo(proxyClient, null, prefix);

    }

    @Override
    public String printView() throws Exception {
        vmInfo_.update();

        if (vmInfo_.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
            System.err
                    .println("ERROR: Could not fetch telemetries - Process terminated?");
            exit();
            return "";
        }
        if (vmInfo_.getState() != VMInfoState.ATTACHED) {
            System.err.println("ERROR: Could not attach to process.");
            exit();
            return "";
        }
        return printVM(vmInfo_);

    }

    private String printVM(VMInfo vmInfo) throws Exception {

        ThreadInfo[] dumpAllThreads = vmInfo.getThreadMXBean().dumpAllThreads(false, false);
        int waiting = 0;
        int blocked = 0;
        int newThreads = 0;
        int runnable = 0;
        int timedWaiting = 0;
        int terminated = 0;
        int total = 0;
        for (int i = 0; i < dumpAllThreads.length; i++) {
            ThreadInfo tInfo = dumpAllThreads[i];
            if (tInfo.getThreadName().startsWith(prefix)) {
                total = total + 1;
                switch (tInfo.getThreadState()) {
                    case WAITING:
                        waiting = waiting + 1;
                        break;
                    case BLOCKED:
                        blocked = blocked + 1;
                        break;
                    case NEW:
                        newThreads = newThreads + 1;
                        break;
                    case RUNNABLE:
                        runnable = runnable + 1;
                        break;
                    case TERMINATED:
                        timedWaiting = timedWaiting + 1;
                        break;
                    case TIMED_WAITING:
                        terminated = terminated + 1;
                        break;
                }
            }

        }
        //System.out.println("Date,TotalThreads,NumOfWaiting,NumOfTimedWaiting,NumOfRunable,NumOfBlock,NumOfTerminated");
        return String
                .format(
                        "%d;%d;%d;%d;%d;%d;%d",
                        System.currentTimeMillis(),
                        total,
                        waiting,
                        timedWaiting,
                        runnable,
                        blocked,
                        terminated);

    }

    @Override
    public boolean isTopBarRequired() {
        return false;
    }

    @Override
    public boolean isClearingRequired() {
        return false;
    }

    @Override
    public String printHeader() throws Exception {
        return ("Date,TotalThreads,NumOfWaiting,NumOfTimedWaiting,NumOfRunable,NumOfBlock,NumOfTerminated");
    }
}
