package net.dishevelled.sqlcounter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer  implements ClassFileTransformer {

    Transform[] availableTransforms = new Transform[] {
        new MySQLTransform(),
        new TomcatTransform(),
        new JettyTransform(),
    };

    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        String fqClassName = className.replace("/", ".");
        byte[] byteCode = classfileBuffer;

        for (Transform transform : availableTransforms) {
            if (transform.appliesTo(fqClassName)) {
                return transform.instrument(fqClassName, byteCode, loader);
            }
        }

        return byteCode;
    }
}
