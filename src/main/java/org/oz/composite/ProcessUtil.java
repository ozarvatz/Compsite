package org.oz.composite;

import java.util.HashMap;

public class ProcessUtil {
    public static final int NOT_SET      = 0;
    public static final int OK           = 200;
    public static final int PROCESS_WARN = 1000;
    public static final int PROCESS_INFO = 1001;
    public static final int PROCESS_FAIL = 1002;

    public static final int NEGATIVE        = 1;
    public static final int NEGATIVE_LITE   = 2;
    public static final int POSITIVE        = 3;
    public static final int POSITIVE_LITE   = 4;
    public static final int NEUTRAL         = 5;

    private static final HashMap<Integer, Integer> SEVERITY = new HashMap<>() {{
        put(OK, POSITIVE);
        put(NOT_SET, NEUTRAL);
        put(PROCESS_WARN, NEGATIVE_LITE);
        put(PROCESS_INFO, NEUTRAL);
        put(PROCESS_FAIL, NEGATIVE);
    }} ;

    public static boolean isSeverer(int oldCode, int newCode) {
        int oldSeverity = SEVERITY.containsKey(oldCode)
                ? SEVERITY.get(oldCode)
                : 0;
        int newSeverity = SEVERITY.containsKey(newCode)
                ? SEVERITY.get(newCode)
                : 0;
        return newSeverity <= oldSeverity;
    }

    public static boolean isBlocker(int newCode) {
        int newSeverity = SEVERITY.containsKey(newCode)
                ? SEVERITY.get(newCode)
                : 0;
        return newSeverity == NEGATIVE;
    }
}
