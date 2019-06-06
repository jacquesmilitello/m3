package io.m3.util.jmh;

import io.m3.util.hash.HashReader;
import io.m3.util.unsafe.UnsafeHelper;

import sun.misc.Unsafe;

public final class HashReader4String extends HashReader {

	private static final Unsafe UNSAFE = UnsafeHelper.getUnsafe();
	
	private final char[] val;
	private long adr;

	public HashReader4String(char[] val) {
		this.val = val;
		this.adr = 16;
	}

	@Override
	public long length() {
		return val.length * 2;
	}

	@Override
	public long getLong() {
		long value = UNSAFE.getLong(val, adr);
		adr += 8;
		return value;
	}

	@Override
	public int getInt() {
		int value = UNSAFE.getInt(val, adr);
		adr += 4;
		return value;
	}

	@Override
	public long getByte() {
		return UNSAFE.getByte(val, adr++);
	}
}