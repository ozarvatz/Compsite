package org.oz.composite;

public interface IChainExecution {
    public String getDescription();
    public boolean execute(IProcessData pData);
    public IChainExecution add(IChainExecution chainExecution);
    public IChainExecution returnTrueOnFalse(boolean isReturnTrueOnFalse);
    public IChainExecution chain(boolean isChained);
}

