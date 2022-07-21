package org.oz.composite;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureCompositeWrapper {
    private String className;
    private Future future;
    private boolean chained;
    private boolean continueOnFalse;

    public FutureCompositeWrapper(Future<?> future,
                                  String className,
                                  boolean chained,
                                  boolean continueOnFalse) {
        this.future = future;
        this.className = className;
        this.chained = chained;
        this.continueOnFalse = continueOnFalse;
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public boolean isChained() {
        return chained;
    }

    public void setChained(boolean chained) {
        this.chained = chained;
    }

    public boolean isContinueOnFalse() {
        return continueOnFalse;
    }

    public void setContinueOnFalse(boolean continueOnFalse) {
        this.continueOnFalse = continueOnFalse;
    }

    public boolean get(long timeout) throws Exception {
        if(0 == timeout) {
            return (Boolean) this.future.get();
        }

        return (Boolean) this.future.get(timeout, TimeUnit.MILLISECONDS);
    }

    public boolean isDone() {return this.future.isDone();}

    public boolean isCancelled() {return this.future.isCancelled(); }

    public boolean cancel(boolean mayInterruptRunning) {
        return this.future.cancel(mayInterruptRunning);
    }

    public boolean returnValueFromFuture(long timeout) throws Exception{
        boolean returnValue = false;
        long startTime = System.currentTimeMillis();
        long elapseJoin = 0;
        try {
            returnValue = this.isContinueOnFalse() ? true : (boolean)this.future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            elapseJoin = System.currentTimeMillis() - startTime;
            String exception = String.format("failed waiting for thread termination, " +
                    "thread class %s, " +
                    "current timeout %d, " +
                    "elapsed %d," +
                    "message %s",
                    this.getClassName(),
                    timeout,
                    elapseJoin,
                    e.getMessage());
            throw new Exception(exception);

        }
        return returnValue;
    }
}
