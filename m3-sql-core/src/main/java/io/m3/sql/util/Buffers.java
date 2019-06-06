package io.m3.sql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public final class Buffers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Buffers.class);
    /**
     * Sun specific mechanisms to clean up resources associated with direct byte buffers.
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends ByteBuffer> SUN_DIRECT_BUFFER = (Class<? extends ByteBuffer>) lookupClassQuietly("sun.nio.ch.DirectBuffer");

    private static final Method SUN_BUFFER_CLEANER;

    private static final Method SUN_CLEANER_CLEAN;

    static {
        Method bufferCleaner = null;
        Method cleanerClean = null;
        if (SUN_DIRECT_BUFFER != null) {
            bufferCleaner = lookupMethodQuietly(SUN_DIRECT_BUFFER, "cleaner");
            cleanerClean = lookupMethodQuietly(lookupClassQuietly("sun.misc.Cleaner"), "clean");
        }
        SUN_BUFFER_CLEANER = bufferCleaner;
        SUN_CLEANER_CLEAN = cleanerClean;
    }

    private Buffers() {
    }

    public static void releaseDirectByteBuffer(ByteBuffer buffer) {
        if (SUN_DIRECT_BUFFER != null && SUN_DIRECT_BUFFER.isAssignableFrom(buffer.getClass())) {
            try {
                Object cleaner = SUN_BUFFER_CLEANER.invoke(buffer, (Object[]) null);
                SUN_CLEANER_CLEAN.invoke(cleaner, (Object[]) null);
            } catch (Exception cause) {
                LOGGER.warn("Failed to clean up Sun specific DirectByteBuffer. [{}]", cause.getMessage());
            }
        }
    }

    static Class<?> lookupClassQuietly(String fcqn) {
        try {
            return Class.forName(fcqn);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static Method lookupMethodQuietly(Class<?> clazz, String method) {
        try {
            return clazz.getMethod(method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
