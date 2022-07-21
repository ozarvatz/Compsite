package org.oz.composite;

public interface IChainExecution {
    public String getDescription();
    public boolean execute(IProcessData pData);
    public IChainExecution add(IChainExecution chainExecution);
    public IChainExecution returnTrueOnFalse(boolean isReturnTrueOnFalse);
    public IChainExecution chain(boolean isChained);

    /**
     * cause this execution element to run asynchronously
     * @param async - default is false for synchronous run.
     * @return this element for further configuration
     */
    public IChainExecution asynchronous(boolean async);

    /**
     * cause this execution element to wait for the all threads define in this chain, till default timeout.
     * @param join
     * @return this element for further configuration
     */
    public IChainExecution join(boolean join);

    /**
     * cause this execution element to wait for the all threads define in this chain, till specific timeout.
     * @param join
     * @param timeout - join timeout
     * @param mayInterruptRunning - cause interrupt if true
     * @return this element for further configuration
     */
    public IChainExecution join(boolean join, long timeout, boolean mayInterruptRunning);



}

