package io.m3.util.hash;

import io.m3.util.unsafe.UnsafeHelper;
import io.m3.util.unsafe.UnsafeString;
import org.junit.jupiter.api.Test;

class XXHashTest {

	@Test
	void test() {

		String value = "Hello World !";
		System.out.println("---> [" + Hashing.xx(123456, value) + "]");

	}

	@Test
	void tests() {

		String value = "Hello World !";
		System.out.println("2---> [" + Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value))) + "]");
	}

	private static class HashReader4String extends HashReader {

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
			long value = UnsafeHelper.getUnsafe().getLong(val, adr);
			adr += 8;
			return value;
		}

		@Override
		public int getInt() {
			int  value = UnsafeHelper.getUnsafe().getInt(val, adr);
			adr +=4;
			return value;
		}

		@Override
		public long getByte() {
			return UnsafeHelper.getUnsafe().getByte(val, adr++);
		}
	}
}
