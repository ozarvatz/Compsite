package org.oz.composite;

import java.util.Hashtable;

public interface IProcessData extends Cloneable {
    public void put(String key, Object value);
    public Object get(String key);
    public void addMessage(int statusCode, String statusMessage);
    public void addTimeConsumed(long runTime);
    public int getStatusCode();
    public String getStatusMessage();
    public Hashtable<String, Object> getMainData();
}
