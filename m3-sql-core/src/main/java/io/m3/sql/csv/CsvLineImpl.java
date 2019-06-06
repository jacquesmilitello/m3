package io.m3.sql.csv;

import io.m3.util.unsafe.UnsafeHelper;
import sun.misc.Unsafe;

import java.util.function.Function;

@SuppressWarnings("all")
final class CsvLineImpl implements CsvLine {

	private final static Unsafe UNSAFE = UnsafeHelper.getUnsafe();
	
	private final long lineStart;
	private final long[] columnAddress;
	private final int line;
	private final int nbColumns;

    public CsvLineImpl(long lineStart, long[] columnAddress, int line, int nbColumns) {
		this.lineStart = lineStart;
		this.columnAddress = columnAddress;
		this.line = line;
		this.nbColumns = nbColumns;
	}

	public byte[] get(int col) {

		if (col == 0) {
			byte[] row = new byte[(int) (this.columnAddress[0] - lineStart) + 1];
			UNSAFE.copyMemory(null, lineStart , row, Unsafe.ARRAY_BYTE_BASE_OFFSET, row.length);
			return row;
		}

		if (nbColumns > col) {
			byte[] row = new byte[(int) (this.columnAddress[col] - this.columnAddress[col-1]) - 1];
			UNSAFE.copyMemory(null, this.columnAddress[col-1] + 2 , row, Unsafe.ARRAY_BYTE_BASE_OFFSET, row.length);
			return row;

		}

		throw new IndexOutOfBoundsException();
	}

	@Override
	public <T> T get(int col, Function<byte[], T> converter) {
		return converter.apply(get(col));
	}

	public int line() {
		return this.line;
	}

	@Override
	public int columns() {
		return this.nbColumns;
	}

}
