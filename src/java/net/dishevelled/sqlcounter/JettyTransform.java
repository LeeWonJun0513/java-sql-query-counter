package net.dishevelled.sqlcounter;

public class JettyTransform extends ServletTransform implements Transform {

    public boolean appliesTo(String className) {
        return className.equals("org.eclipse.jetty.server.handler.HandlerWrapper");
    }

    protected String[] methodsToTrace() {
        return new String[] { "handle" };
    }

}
