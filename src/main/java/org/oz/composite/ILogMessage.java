package org.oz.composite;

public interface ILogMessage {
    public void setCaller(String caller);
    public void addStatusMessage(int statusCode, String statusMessage);
    public void setTimeConsumed(long timeConsumed);
    public String toJson();
}
