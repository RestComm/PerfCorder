package org.restcomm.perfcorder.collector;

import java.util.EventListener;


public interface StateListener extends EventListener{
    public void stateChanged(VMInfo info, VMInfoState newState);
}
