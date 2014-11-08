package net.dishevelled.sqlcounter;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("Registering counter");
        inst.addTransformer(new Transformer());
    }
}
