package org.oz.composite;

import java.util.*;
import java.util.concurrent.Future;

import org.apache.commons.lang3.SerializationUtils;


public class ProcessData implements IProcessData, Cloneable {
    static final private String LOG_KEY = "log_key";
    static final private String STATUS_MESSAGE = "status_message";
    static final private String STATUS_CODE = "status_code";

    private Hashtable<String, Object> mainData;
    private Hashtable<String, List<ILogMessage>> log;
    private List<FutureCompositeWrapper> futures;

    //TODO add jsonNode for messages

    public ProcessData() {
        this.mainData = new Hashtable<>();
        this.log = new Hashtable<>();
        this.futures = new ArrayList<FutureCompositeWrapper>();
    }

    public ProcessData(IProcessData pData) {
        this();
        this.mainData = SerializationUtils.clone(pData.getMainData());
        this.log = new Hashtable<>();
    }

    @Override
    public void put(String key, Object value) {
        this.mainData.put(key, value);
    }

    @Override
    public Object get(String key) {
        return this.mainData.get(key);
    }

    @Override
    public void addMessage(int statusCode, String statusMessage) {
        String caller = this.getCaller();
        ILogMessage currentLog = this.log.containsKey(caller)
                ? (ILogMessage) this.log.get(caller).get(this.log.get(caller).size() - 1)
                : new LogMessage();
        currentLog.setCaller(caller);
        currentLog.addStatusMessage(statusCode, statusMessage);

        List<ILogMessage> callerLogList = this.log.containsKey(caller)
                ? this.log.get(caller)
                : new ArrayList<ILogMessage>();
        callerLogList.add(currentLog);
        log.put(caller, callerLogList);
        List<String> msgList = this.mainData.containsKey(LOG_KEY)
                ? (List) this.mainData.get(LOG_KEY)
                : new LinkedList<>();
        String line = String.format("%s, %d, %s", caller, statusCode, statusMessage);
        msgList.add(line);
        this.mainData.put(LOG_KEY, msgList);
        this.setStatusMessageAndCode(statusCode, line);
    }

    @Override
    public void addTimeConsumed(String caller, long timeConsumed) {
        caller = null == caller ? this.getCaller() : caller;
        ILogMessage currentLog = this.log.containsKey(caller)
                ? (ILogMessage) this.log.get(caller).get(this.log.get(caller).size() - 1)
                : new LogMessage();
        currentLog.setCaller(caller);
        currentLog.setTimeConsumed(timeConsumed);
    }


    @Override
    public int getStatusCode() {
        if (this.mainData.containsKey(STATUS_CODE)) {
            return (int) this.mainData.get(STATUS_CODE);
        }
        return 0;
    }

    @Override
    public String getStatusMessage() {
        if (this.mainData.containsKey(STATUS_MESSAGE)) {
            return (String) this.mainData.get(STATUS_MESSAGE);
        }
        return null;
    }

    private String getCaller() {
        //TODO fetch caller
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        int index = 0;
        while (!elements[index].getMethodName().equals("exec") && !elements[index].getClassName().contains("ChainExcecutionAbs")) {
            index++;
        }

        index--;
        return index > 0
                ? String.format("%s::%s", elements[index].getClassName(), elements[index].getMethodName())
                : "unknown";
    }

    private void setStatusMessageAndCode(int newCode, String newMessage) {
        int currentCode = this.mainData.containsKey(STATUS_CODE)
                ? (int) this.mainData.get(STATUS_CODE)
                : 0;
        if (ProcessUtil.isSeverer(currentCode, newCode)) {
            this.mainData.put(STATUS_CODE, newCode);
            this.mainData.put(STATUS_MESSAGE, newMessage);
        }
    }

    @Override
    public Hashtable<String, Object> getMainData() {
        return mainData;
    }

    @Override
    public void addFuture(FutureCompositeWrapper future) {
        this.futures.add(future);
    }

    @Override
    public List<FutureCompositeWrapper> getFutures() {
        return this.futures;
    }

    @Override
    public void clearFutures() {
        this.futures.clear();
    }

}

