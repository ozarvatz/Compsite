package org.oz.composite;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class ChainExcecutionAbs implements IChainExecution {

    private LinkedList<IChainExecution> children;
    private boolean returnOnFalse;
    private boolean chained;
    abstract public boolean run(IProcessData pData);
    private long beforeTimestamp;

    public ChainExcecutionAbs() {
        this.children       = null;
        this.chained        = true;
        this.returnOnFalse  = false;
        this.beforeTimestamp = 0;
    }
    @Override
    public boolean execute(IProcessData pData) {
        IProcessData pd = this.chained ? pData : new ProcessData(pData);
        boolean returnValue = this.exec(pd);
        return this.calculateReturnVal(returnValue);
    }

    private boolean calculateReturnVal(boolean currentRetVal) {
        return this.returnOnFalse ? true : currentRetVal;
    }

    private boolean exec(IProcessData pData) {
        this.beforeRun(pData);
        boolean returnValue = true;
        Iterator<IChainExecution> processIT = null == this.children
                ? null
                : this.children.iterator();
        if(null != processIT) {
            while(returnValue && processIT.hasNext()){
                IChainExecution process = processIT.next();
                returnValue = process.execute(pData);
            }
        }

        if(returnValue || this.returnOnFalse) {
            returnValue = this.run(pData);
        }

        this.afterRun(pData);
        return returnValue || this.returnOnFalse;
    }
    @Override
    public IChainExecution add(IChainExecution chainExecution) {
        if(null == this.children) {
            this.children = new LinkedList<>();
        }
        this.children.add(chainExecution);
        return this;
    }

    @Override
    public IChainExecution returnTrueOnFalse(boolean isReturnTrueOnFalse) {
        this.returnOnFalse = isReturnTrueOnFalse;
        return this;
    }

    @Override
    public IChainExecution chain(boolean isChained) {
        this.chained = isChained;
        return null;
    }

    protected void beforeRun(IProcessData pData) {
        this.beforeTimestamp = System.currentTimeMillis();
    }

    protected void afterRun(IProcessData pData) {
        long diff = System.currentTimeMillis() - this.beforeTimestamp;
        pData.addTimeConsumed(diff);
    }
}
