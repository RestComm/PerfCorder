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

import org.apache.log4j.Logger;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

/**
 * "detail" view, printing detail metrics of a specific jvm in a vmstat manner.
 *
 *
 * @author
 *
 */
public class VMDetailStatView extends AbstractConsoleView {

    private static Logger logger = org.apache.log4j.Logger.getLogger(VMDetailStatView.class.getName());
    private VMInfo vmInfo_;

    public VMDetailStatView(int vmid, Integer width) throws Exception {
        super(width);
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        vmInfo_ = VMInfo.processNewVM(localVirtualMachine, vmid);
    }

    public VMDetailStatView(String url, Integer width) throws Exception {
        super(width);
        ProxyClient proxyClient = ProxyClient.getProxyClient(url,
                System.getenv("PERF_USER"),
                System.getenv("PERF_PSW"));
        proxyClient.connect();

        vmInfo_ = new VMInfo(proxyClient, null, null);
    }

    @Override
    public String printView() throws Exception {
        vmInfo_.update();
        return printVM(vmInfo_);

    }

    private String printVM(VMInfo vmInfo) throws Exception {

        String deadlockState = "C";
        if (vmInfo.hasDeadlockThreads()) {
            deadlockState = "!D";
        }

        return String
                .format(
                        "%s,%.2f,%.2f,%d,%s,%s",
                        toMB(vmInfo.getHeapUsed()),
                        vmInfo.getCpuLoad() * 100,
                        vmInfo.getGcLoad() * 100,
                        vmInfo.getThreadCount(),
                        deadlockState,
                        toMB(vmInfo.getOldPoolMXBean().getUsage().getUsed()));

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
        return ("mem,cpu,gcCpu,threads,deadLock,oldMem");
    }
}
