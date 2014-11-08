package net.dishevelled.sqlcounter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// Records and reports call counts.
public class SQLQueryCounter {

    private static Map<String, AtomicInteger> queryCounts;

    static {
        queryCounts = new ConcurrentHashMap<String, AtomicInteger>();
    }

    private static String currentThread() {
        Thread t = Thread.currentThread();
        return t.getName() + "@" + t.getId();
    }


    public static void clearCount() {
        clearCount(currentThread());
    }

    public static void clearCount(String identifier) {
        queryCounts.put(identifier, new AtomicInteger(0));
    }


    public static boolean increment() {
        return increment(currentThread());
    }


    public static boolean increment(String identifier) {
        AtomicInteger count = queryCounts.get(identifier);

        if (count != null) {
            count.getAndIncrement();
            return true;
        }

        return false;
    }


    public static int getCount() {
        return getCount(currentThread());
    }

    public static int getCount(String identifier) {
        AtomicInteger count = queryCounts.get(identifier);

        if (count != null) {
            return count.intValue();
        } else {
            return -1;
        }
    }


    private static String truncateClassName(String className) {
        return className.replaceAll("^.*\\.(.*\\..*)$", "$1");
    }


    public static String shortStackTrace(int max) {
        StackTraceElement[] stack = new Exception().getStackTrace();
        StringBuilder sb = new StringBuilder();

        int emitted = 0;
        // Start from 1 to skip ourselves
        for (int i = 1; emitted < max && i < stack.length; i++) {
            StackTraceElement frame = stack[i];

            if (frame.getClassName().matches("^.*(mysql|oracle|dbcp|hibernate|wicket|spring|reflect|sun\\.proxy).*$")) {
                // Skip over these frames because they're mostly plumbing.  Show me an application class! :P
                continue;
            }

            sb.append(String.format("%s.%s:%d ",
                                    truncateClassName(frame.getClassName()),
                                    frame.getMethodName(),
                                    frame.getLineNumber()));
            emitted++;
        }

        return sb.toString().trim();
    }
}
