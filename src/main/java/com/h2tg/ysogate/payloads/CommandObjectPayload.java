package com.h2tg.ysogate.payloads;


import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

import com.h2tg.ysogate.Main;
import org.reflections.Reflections;


@SuppressWarnings ( "rawtypes" )
public interface CommandObjectPayload<T> {

    /*
     * return armed payload object to be serialized that will execute specified
     * command on deserialization
     */
    public T getObject(String command) throws Exception;

    public static class Utils {

        // get payload classes by classpath scanning
        public static Set<Class<? extends CommandObjectPayload>> getPayloadClasses() {
            final Reflections reflections  = new Reflections(CommandObjectPayload.class.getPackage().getName());
            final Set<Class<? extends CommandObjectPayload>> payloadTypes = reflections.getSubTypesOf(CommandObjectPayload.class);
            for (Iterator<Class<? extends CommandObjectPayload>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends CommandObjectPayload> pc = iterator.next();
                if (pc.isInterface() || Modifier.isAbstract(pc.getModifiers())) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }


        @SuppressWarnings("unchecked")
        public static Class<? extends CommandObjectPayload> getPayloadClass(final String className) {
            Class<? extends CommandObjectPayload> clazz = null;
            try {
                clazz = (Class<? extends CommandObjectPayload>) Class.forName(className);
            } catch (Exception ignored) {
            }
            if (clazz == null) {
                try {
                    return clazz = (Class<? extends CommandObjectPayload>) Class
                            .forName(Main.class.getPackage().getName() + ".payloads.gadgets." + className);
                } catch (Exception ignored) {
                }
            }
            if (clazz != null && !CommandObjectPayload.class.isAssignableFrom(clazz)) {
                clazz = null;
            }
            return clazz;
        }


        public static Object makePayloadObject(String payloadType, String payloadArg) {
            final Class<? extends CommandObjectPayload> payloadClass = getPayloadClass(payloadType);
            if (payloadClass == null || !CommandObjectPayload.class.isAssignableFrom(payloadClass)) {
                throw new IllegalArgumentException("Invalid payload type '" + payloadType + "'");

            }

            final Object payloadObject;
            try {
                final CommandObjectPayload payload = payloadClass.newInstance();
                payloadObject = payload.getObject(payloadArg);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to construct payload", e);
            }
            return payloadObject;
        }


        @SuppressWarnings("unchecked")
        public static void releasePayload(CommandObjectPayload payload, Object object) throws Exception {
            if (payload instanceof ReleaseableObjectPayload) {
                ((ReleaseableObjectPayload) payload).release(object);
            }
        }


        public static void releasePayload(String payloadType, Object payloadObject) {
            final Class<? extends CommandObjectPayload> payloadClass = getPayloadClass(payloadType);
            if (payloadClass == null || !CommandObjectPayload.class.isAssignableFrom(payloadClass)) {
                throw new IllegalArgumentException("Invalid payload type '" + payloadType + "'");

            }

            try {
                final CommandObjectPayload payload = payloadClass.newInstance();
                releasePayload(payload, payloadObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
