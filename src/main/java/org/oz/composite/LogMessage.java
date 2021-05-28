package org.oz.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMessage implements ILogMessage{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogMessage.class);
    private String caller;
    private ArrayList<String> messages;
    private ArrayList<Integer> statuses;
    private long timeConsumed;

    public LogMessage() {
        this.statuses = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    @Override
    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    public void addStatusMessage(int statusCode, String statusMessage) {
        this.messages.add(statusMessage);
        this.statuses.add(statusCode);
        String threadName = Thread.currentThread().getName();
        String line = String.format("threadName: %s, caller: %s, statusCode: %d, message: %s",
                threadName,
                this.caller,
                statusCode,
                statusMessage
        );
        switch (statusCode) {
            case ProcessUtil.NEGATIVE:
                LOGGER.error(line);
                break;
            case ProcessUtil.NEGATIVE_LITE:
                LOGGER.warn(line);
                break;
            default:
                LOGGER.info(line);
                break;

        }
    }

    @Override
    public void setTimeConsumed(long timeConsumed) {

        this.timeConsumed = timeConsumed;
        String threadName = Thread.currentThread().getName();

        LOGGER.info(String.format("threadName: %s, caller: %s, time consumed: %d",
                threadName,
                this.caller,
                timeConsumed
        ));

    }


    @Override
    public String toJson() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("caller = %s, time consumed = %, messages = %s, statuses = %s",
                this.caller,
                this.timeConsumed,
                this.messages.toString(),
                this.statuses.toString());
    }
}
