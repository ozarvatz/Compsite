package org.oz.composite;

import java.util.Hashtable;
import java.util.List;

public interface IProcessData extends Cloneable {
    public void put(String key, Object value);
    public Object get(String key);
    public void addMessage(int statusCode, String statusMessage);
    public void addTimeConsumed(String caller, long runTime);
    public int getStatusCode();
    public String getStatusMessage();
    public Hashtable<String, Object> getMainData();
    public void addFuture(FutureCompositeWrapper future);
    public List<FutureCompositeWrapper> getFutures();
    public void clearFutures();
}
