package org.restcomm.perfcorder.jenkins;

/**
 * @author Gregory Boissinot
 */
public class PerfCorderException extends Exception {

    public PerfCorderException(String s) {
        super(s);
    }

    public PerfCorderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PerfCorderException(Throwable throwable) {
        super(throwable);
    }
}
