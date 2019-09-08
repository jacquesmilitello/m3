package io.m3.util.unsafe;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("all")
public final class UnsafeHelper {

	private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

	private static final Unsafe UNSAFE;

	private static final Architecture ARCH;

	private static com.sun.management.HotSpotDiagnosticMXBean hotspotMBean;

	static {

		final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
			/** {@inheritDoc} */
			@Override
			public Unsafe run() throws NoSuchFieldException, IllegalAccessException {
				Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
				theUnsafe.setAccessible(true);
				return (Unsafe) theUnsafe.get(null);
			}
		};

		try {
			UNSAFE = AccessController.doPrivileged(action);
		} catch (PrivilegedActionException cause) {
			throw new RuntimeException("Unable to load unsafe", cause);
		}

		String bits = System.getProperty("sun.arch.data.model");
		if (bits.equals("32")) {
			ARCH = Architecture.X86;
		} else if (bits.equals("64") && getUseCompressedOopsVMOption()) {
			ARCH = Runtime.getRuntime().maxMemory() <= 2058354688 ? Architecture.X64WITH32BITCOMPRESSEDOOPS
					: Architecture.X64WITHCOMPRESSEDOOPS;
		} else {
			ARCH = Architecture.X64;
		}
	}

	private static final long OBJECT_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);

	private UnsafeHelper() {
	}

	public static boolean getUseCompressedOopsVMOption() {
		return Boolean.valueOf(getHotSpotMBean().getVMOption("UseCompressedOops").getValue());
	}

	public static int arrayCharBaseOffset() {
		return UNSAFE.ARRAY_CHAR_BASE_OFFSET;
	}

	public static Unsafe getUnsafe() {
		return UNSAFE;
	}

	public static long toAddress(Object obj) {
		Object[] array = new Object[] { obj };
	
		switch (ARCH) {
		case X86:
			return normalize(UNSAFE.getInt(array, OBJECT_ARRAY_BASE));
		case X64:
			return UNSAFE.getLong(array, OBJECT_ARRAY_BASE);
		case X64WITHCOMPRESSEDOOPS:
			return (normalize(UNSAFE.getInt(array, OBJECT_ARRAY_BASE)) << 3);
		default:
			return normalize(UNSAFE.getInt(array, OBJECT_ARRAY_BASE));

		}
	}

	private static long normalize(int value) {
		return value & 0xFFFFFFFFL;
	}

	public static long getFieldOffset(Class<?> clazz, String field) {
		try {
			return UNSAFE.objectFieldOffset(clazz.getDeclaredField(field));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static com.sun.management.HotSpotDiagnosticMXBean getHotSpotMBean() {
		if (hotspotMBean == null) {
			try {
				hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(),
						HOTSPOT_BEAN_NAME, com.sun.management.HotSpotDiagnosticMXBean.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hotspotMBean;
	}
}