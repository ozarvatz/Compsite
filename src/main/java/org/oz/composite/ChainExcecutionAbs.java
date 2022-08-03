package org.oz.composite;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ChainExcecutionAbs implements IChainExecution {

    private static final int CONTINUE_TO_RUN             = 1;
    private static final int STOP_RUNNING_RETURN_FALSE   = 2;
    private static final int STOP_RUNNING_RETURN_TRUE    = 3;

    private LinkedList<IChainExecution> children;
    private boolean returnTrueOnFalse;
    private boolean chained;
    private long beforeTimestamp;
    private boolean runAsync;
    private boolean join;
    private long joinTimeout;
    private boolean joinMayInterruptRunning;
    private Set<?> customSet;
    private String customKey;
    private Set<Integer> statusOnly;
    private ForeachCompoHelper foreachCompoHelper;

    private ExecutorService executorService;


    abstract public boolean run(IProcessData pData);

    public ChainExcecutionAbs() {
        this.children       = null;
        this.chained        = true;
        this.returnTrueOnFalse = false;
        this.beforeTimestamp = 0;
        this.join           = false;
        this.joinTimeout    = 2000;
        this.executorService = Executors.newFixedThreadPool(5);
        this.statusOnly = null;
    }

    @Override
    public boolean execute(IProcessData pData) {
        //copy pData if this process is not chained
        IProcessData pd = this.chained ? pData : new ProcessData(pData);

        boolean returnValue = true;
        if(this.runAsync) {
            Future<?> future = this.executorService.submit(() -> {
                return this.exec(pd);
            });

            pData.addFuture(new FutureCompositeWrapper(future, this.getClass().getName(), this.chained, this.returnTrueOnFalse));

            pData.addMessage(ProcessUtil.PROCESS_INFO,
                    String.format("start new thread for %s", this.getClass().getSimpleName()));

        } else {
            returnValue = this.exec(pd);

        }

        if(this.join) {
            returnValue = this.wait(pData);
            pData.clearFutures();
        }
        return this.calculateReturnVal(returnValue);
    }

    private boolean calculateReturnVal(boolean currentRetVal) {
        return this.returnTrueOnFalse ? true : currentRetVal;
    }

    private boolean exec(IProcessData pData) {
        this.beforeRun(pData);
        boolean returnValue = true;

        if(STOP_RUNNING_RETURN_TRUE == this.calcIfInCustomOnly(pData)) {return true;}

        Iterator<IChainExecution> processIT = null == this.children
                ? null
                : this.children.iterator();
        if(null != processIT) {
            while(processIT.hasNext()){
                ChainExcecutionAbs process = (ChainExcecutionAbs) processIT.next();
                if(returnValue || process.isInCustom(pData)) {
                    returnValue = process.execute(pData);
                } else {
                    break;
                }
            }
        }

        if(returnValue || this.returnTrueOnFalse) {
            returnValue = this.run(pData);
        }

        this.afterRun(pData);
        return returnValue || this.returnTrueOnFalse;
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
        this.returnTrueOnFalse = isReturnTrueOnFalse;
        return this;
    }

    @Override
    public IChainExecution chain(boolean isChained) {
        this.chained = isChained;
        return null;
    }

    @Override
    public IChainExecution asynchronous(boolean async) {
        this.runAsync = async;
        return this;
    }

    @Override
    public IChainExecution join(boolean join) {
        this.join = join;
        return this;
    }

    @Override
    public IChainExecution join(boolean join, long timeout, boolean mayInterruptRunning) {
        this.join = join;
        this.joinTimeout = timeout;
        this.joinMayInterruptRunning = mayInterruptRunning;
        return this;
    }

    protected void beforeRun(IProcessData pData) {
        this.beforeTimestamp = System.currentTimeMillis();
    }

    protected void afterRun(IProcessData pData) {
        long diff = System.currentTimeMillis() - this.beforeTimestamp;
        pData.addTimeConsumed(this.getClass().getName(), diff);
    }


    private boolean wait(IProcessData pData) {
        List<FutureCompositeWrapper> futures = pData.getFutures();
        boolean joinedReturnValue = true;
        boolean isCanceled = true;
        long startJoin = 0;
        long elapseJoin = 0;
        long currentTimeout = this.joinTimeout;
        boolean infinite = 0 == this.joinTimeout;
        for(FutureCompositeWrapper furure : futures) {
            if(furure.isDone()){
                try {
                    joinedReturnValue &=  furure.returnValueFromFuture(this.joinTimeout);
                }catch (Exception e) {
                    pData.addMessage(ProcessUtil.PROCESS_FAIL,
                            e.getMessage());
                }
            } else if(!furure.isCancelled()){
                try{
                    startJoin = System.currentTimeMillis();
                    boolean retVal = furure.get(this.joinTimeout);
                    joinedReturnValue &= (furure.isChained() && furure.isContinueOnFalse())
                            ? true
                            : retVal;
                    elapseJoin = System.currentTimeMillis() - startJoin;
                    currentTimeout -= elapseJoin;
                    currentTimeout = currentTimeout < 0 ? 10 : currentTimeout;
                }catch (Exception ee) {
                    if(furure.isDone()) {
                        boolean retVal = true;
                        try {retVal = furure.get(0);} catch(Exception eee) { eee.printStackTrace();}
                        joinedReturnValue &= furure.isChained() && furure.isContinueOnFalse()
                                ? true
                                : retVal;
                    } else {
                        //is canceled now
                        isCanceled = furure.isCancelled() ? false : furure.cancel(this.joinMayInterruptRunning);
                        elapseJoin = (System.currentTimeMillis() - startJoin);
                        currentTimeout -= elapseJoin;
                        currentTimeout = currentTimeout < 0 ? 10 : currentTimeout;
                        pData.addMessage(ProcessUtil.PROCESS_FAIL,
                                String.format("failed waiting to thread termination, " +
                                        "thrad name %s, " +
                                        "timeout %d (current timeout %d, elapse time %d), " +
                                        "isCanceled %b, message %s",
                                        furure.getClassName(),
                                        this.joinTimeout,
                                        currentTimeout,
                                        elapseJoin,
                                        isCanceled,
                                        this.joinMayInterruptRunning,
                                        ee.getMessage()));
                    }
                }
            }
        }
        return joinedReturnValue;
    }



    @Override
    public IChainExecution runIfInCustomOnly(String customKey, Set<?> customSet) {
        this.customKey = customKey;
        this.customSet = customSet;
        return this;
    }

    protected boolean isInCustom(IProcessData pd) {
        Object obj = pd.get(this.customKey);
        return (null != this.customSet && this.customSet.contains(obj));
    }

    private int calcIfInCustomOnly(IProcessData pd) {
        if(null == this.customSet) {
            return CONTINUE_TO_RUN;
        }

        if(!this.isInCustom(pd)) {
            pd.addMessage(ProcessUtil.OK,
                    String.format("process skipped, custom role %s is not in customSet %s",
                            this.customKey,
                            this.customSet.toString()));
            return STOP_RUNNING_RETURN_TRUE;
        }
        return CONTINUE_TO_RUN;
    }


    @Override
    public IChainExecution foreach(String queueName, boolean returnOnTrue, boolean returnOnFalse, int limit) {
        this.foreachCompoHelper = new ForeachCompoHelper(queueName, returnOnTrue, returnOnFalse, limit);
        return this;
    }

    @Override
    public Object poll(ProcessData pData) {
        if(ForeachCompoHelper.VALID == this.foreachCompoHelper.validate(pData)) {
            Queue<?> element = (Queue<?>) pData.get(this.foreachCompoHelper.getQueueName());
            if(null == element) { return null; }
            if(element.size() > ((ThreadPoolExecutor) this.executorService).getActiveCount() + 3) {
                return element.poll();
            } else {
                synchronized (element) {
                    if(element.size() > 0) {
                        return element.poll();
                    }
                }
            }
        }
        return null;
    }

    private boolean calcForeachLoop(ProcessData pData, int[] loop) {
        loop[0] = 1;
        if(null == this.foreachCompoHelper) { return true; }
        switch (this.foreachCompoHelper.validate(pData)) {
            case ForeachCompoHelper.VALID:
                this.foreachCompoHelper.
        }
    }
}
