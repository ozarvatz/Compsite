package org.oz.composite;

import java.util.Set;

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

    /**
     * cause this element to execute if pData.get(customKey) define in customSet.
     * @param customKey - the field in pData to find in customSet.
     * @param customSet
     * @return this
     */
    public IChainExecution runIfInCustomOnly(String customKey, Set<?> customSet);


    /**
     * run the current element on a loop.
     * the loop is based on a ConcurrentLinkedQueue located at pData.
     * this element can get the next data element from the queue by using the member function:
     * getNextForeachElement().
     * @param queueName - the key for the queue located at pData.
     * @param returnOnTrue - if true, stops the loop if the current element execution return true.
     * @param returnOnFalse - if true, stops the loop if the current element execution return false.
     * @param limit - stop the loop if iterations is abovelimit.
     *              if limmit == 0 then the loop will continue till getNextForeachElement() return's null.
     * @return this
     */
    public IChainExecution foreach(String queueName, boolean returnOnTrue, boolean returnOnFalse, int limit);

    /**
     * return the next element from a queue defined at foreach config.
     * @param pData - the processData that contains the queue.
     * @return the element.
     */
    public Object poll(IProcessData pData);
}

