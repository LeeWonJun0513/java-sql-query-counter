package net.dishevelled.sqlcounter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


public class MySQLTransform implements Transform {

    // MySQL's PreparedStatement is a subclass of StatementImpl, but the query methods don't call super(), so we're not double counting here.
    private String[] TARGET_CLASSES = {"com.mysql.jdbc.PreparedStatement", "com.mysql.jdbc.StatementImpl"};
    private String[] METHOD_NAMES = new String[] {"executeQuery", "execute", "executeUpdate"};

    public boolean appliesTo(String className) {
        for (int i = 0; i < TARGET_CLASSES.length; i++) {
            String targetClass = TARGET_CLASSES[i];

            if (targetClass.equals(className)) {
                return true;
            }
        }

        return false;
    }


    public byte[] instrument(String className, byte[] defaultBytes, ClassLoader loader) {
        byte[] result = defaultBytes;

        try {
            ClassPool cp = ClassPool.getDefault();
            cp.appendClassPath(new javassist.LoaderClassPath(loader));
            CtClass preparedStatement = cp.get(className);

            for (String methodName : METHOD_NAMES) {
                CtMethod executeQuery = preparedStatement.getDeclaredMethod(methodName);
                String itemToLog = (className.indexOf("Prepared") >= 0) ? "this" : "sql";
                executeQuery.insertBefore("{" +
                                          "if (net.dishevelled.sqlcounter.SQLQueryCounter.increment()) { " +
                                          String.format("System.err.println(Thread.currentThread() + \" [SQL]> %s.%s: \" + %s);", className, methodName, itemToLog) +
                                          "System.err.println(\"[STACK]> \" + net.dishevelled.sqlcounter.SQLQueryCounter.shortStackTrace(10) + \"\\n\");" +
                                          "}" +
                                          "}");
            }

            result = preparedStatement.toBytecode();
            preparedStatement.detach();

            System.err.println("\n\n*** Instrumented " + className + "\n\n");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return result;
    }
}

