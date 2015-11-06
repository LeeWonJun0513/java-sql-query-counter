package net.dishevelled.sqlcounter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


public class PostgresTransform implements Transform {

    private String[] TARGET_CLASSES = {"org.postgresql.jdbc2.AbstractJdbc2Statement" };
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
                for (CtClass[] variant : new CtClass[][] { new CtClass[] {}, new CtClass[] { cp.get("java.lang.String") } }) {
                    CtMethod method = preparedStatement.getDeclaredMethod(methodName, variant);
                    String itemToLog = (variant.length == 0) ? "this" : "p_sql";
                    method.insertBefore("{" +
                            "if (net.dishevelled.sqlcounter.SQLQueryCounter.increment()) { " +
                            String.format("System.err.println(Thread.currentThread() + \" [SQL]> %s.%s: \" + %s);", className, methodName, itemToLog) +
                            "System.err.println(\"[STACK]> \" + net.dishevelled.sqlcounter.SQLQueryCounter.shortStackTrace(10) + \"\\n\");" +
                            "}" +
                            "}");
                }
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

