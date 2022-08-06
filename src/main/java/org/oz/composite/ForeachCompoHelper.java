package org.oz.composite;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ForeachCompoHelper {
    public static final int VALID                                = 1;
    public static final int NOT_VALID_QUEUE_SIZE_0               = 2;
    public static final int NOT_VALID_NOT_CONCORENT_LINKED_QUEUE = 3;
    public static final int NOT_VALID_NULL_QUEUE                 = 4;

    private String queueName;
    private boolean returnOnTrue;
    private boolean returnOnFalse;
    private int limit;
    private int iterations;

    public ForeachCompoHelper(String queueName, boolean returnOnTrue, boolean returnOnFalse, int limit) {
        this.queueName = queueName;
        this.returnOnFalse = returnOnFalse;
        this.returnOnTrue = returnOnTrue;
        this.limit = limit;
        this.iterations = 0;
    }


    public String getQueueName() {
        return queueName;
    }

    public boolean isReturnOnTrue() {
        return returnOnTrue;
    }

    public boolean isReturnOnFalse() {
        return returnOnFalse;
    }

    public int getLimit() {
        return limit;
    }

    public int getIterations() {
        return iterations;
    }
    
    public int validate(final IProcessData pData) {
        Queue<?> queue = (Queue<?>) pData.get(this.queueName);
        if(null == queue) { return NOT_VALID_NULL_QUEUE; }
        if(queue.size() == 0) { return NOT_VALID_QUEUE_SIZE_0; }
        if(!(queue instanceof ConcurrentLinkedQueue)) { return NOT_VALID_NOT_CONCORENT_LINKED_QUEUE; }
        this.iterations = this.limit == 0
                ? queue.size()
                : Math.min(queue.size(), this.limit);

        return VALID;
    }


}
