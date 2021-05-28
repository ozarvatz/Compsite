package org.oz.composite;

import java.util.*;

import org.apache.commons.lang3.SerializationUtils;


public class ProcessData implements IProcessData, Cloneable {
    static final private String LOG_KEY = "log_key";
    static final private String STATUS_MESSAGE = "status_message";
    static final private String STATUS_CODE = "status_code";

    private Hashtable<String, Object> mainData;
    private Hashtable<String, List<ILogMessage>> log;

    //TODO add jsonNode for messages

    public ProcessData() {
        this.mainData = new Hashtable<>();
        this.log = new Hashtable<>();
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
                ? (ILogMessage) this.log.get(caller)
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
    public void addTimeConsumed(long timeConsumed) {
        String caller = this.getCaller();
        ILogMessage currentLog = this.log.containsKey(caller)
                ? (ILogMessage) this.log.get(caller).get(this.log.get(caller).size() - 1)
                : new LogMessage();
        currentLog.setTimeConsumed(timeConsumed);
    }


    @Override
    public int getStatusCode() {
        if(this.mainData.containsKey(STATUS_CODE)) {
            return (int) this.mainData.get(STATUS_CODE);
        }
        return 0;
    }

    @Override
    public String getStatusMessage() {
        if(this.mainData.containsKey(STATUS_MESSAGE)) {
            return (String) this.mainData.get(STATUS_MESSAGE);
        } 
        return null;
    }

    private String getCaller() {
        //TODO fetch caller
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        return elements.length > 5
            ? String.format("%s::%s", elements[4].getClassName(), elements[4].getMethodName())
            : "unknown";
    }

    private void setStatusMessageAndCode(int newCode, String newMessage) {
        int currentCode = this.mainData.containsKey(STATUS_CODE)
                ? (int) this.mainData.get(STATUS_CODE)
                : 0;
        if(ProcessUtil.isSeverer(currentCode, newCode)) {
            this.mainData.put(STATUS_CODE, newCode);
            this.mainData.put(STATUS_MESSAGE, newMessage);
        }
    }

    @Override
    public Hashtable<String, Object> getMainData() {
        return mainData;
    }

}
