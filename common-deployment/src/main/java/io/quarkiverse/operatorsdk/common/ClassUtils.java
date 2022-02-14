package io.quarkiverse.operatorsdk.common;

import static io.quarkiverse.operatorsdk.common.Constants.IGNORE_RECONCILER;
import static io.quarkiverse.operatorsdk.common.Constants.RECONCILER;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

public class ClassUtils {

    private ClassUtils() {
    }

    /**
     * Only retrieve {@link io.javaoperatorsdk.operator.api.reconciler.Reconciler} implementations that should be considered by
     * the extension, excluding the SDK's own implementations.
     * 
     * @param index
     * @param log
     * @return
     */
    public static Stream<ClassInfo> getKnownReconcilers(IndexView index, Logger log) {
        return index.getAllKnownImplementors(RECONCILER).stream().filter(ci -> keep(ci, log));
    }

    private static boolean keep(ClassInfo ci, Logger log) {
        final var consideredClassName = ci.name();
        if (Modifier.isAbstract(ci.flags())) {
            log.debugv("Skipping ''{0}'' reconciler because it''s abstract", consideredClassName);
            return false;
        }

        // Ignore SDK internal Reconciler implementations
        return !ci.annotations().containsKey(IGNORE_RECONCILER);
    }
}