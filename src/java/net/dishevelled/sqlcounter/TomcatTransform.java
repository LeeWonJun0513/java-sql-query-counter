package net.dishevelled.sqlcounter;

public class TomcatTransform extends ServletTransform implements Transform {

    public boolean appliesTo(String className) {
        // Matching on StandardEngineValve to try to only hook in for top-level
        // requests (e.g. avoiding resetting the counter when one servlet
        // forwards to another)
        return className.equals("org.apache.catalina.core.StandardEngineValve");
    }

    protected String[] methodsToTrace() {
        return new String[] { "invoke" };
    }

}
