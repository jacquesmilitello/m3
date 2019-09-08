package io.m3.sql.csv;

import java.nio.Buffer;
import java.nio.MappedByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.m3.sql.util.Buffers;
import io.m3.util.unsafe.UnsafeHelper;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
final class CsvReaderImpl implements CsvReader {

    /**
     * Unsafe reference;
     */
    private static final Unsafe UNSAFE = UnsafeHelper.getUnsafe();

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvReaderImpl.class);

    private static final byte LF = '\n';
    private static final byte CR = '\r';
    private static final byte QUOTE = '\"';
    private static final byte COMMA = ',';


    private final MappedByteBuffer buffer;
    private final long address;
    private final long addressMax;
    private final FileEndOfLine fileEndOfLine;

    private long offset;
    private int line = 0;

    CsvReaderImpl(MappedByteBuffer buffer) {
        this.buffer = buffer;
        this.address = UNSAFE.getLong(buffer, UnsafeHelper.getFieldOffset(Buffer.class, "address"));
        this.addressMax = address + buffer.limit();
        this.offset = this.address;

        ByteOrderMark bom = ByteOrderMark.read(buffer);
        if (bom != null) {
            buffer.position(bom.getValue().length);
            this.offset += bom.getValue().length;
        }
        fileEndOfLine = findEndOfLine();

        LOGGER.info("find file eol [{}]", fileEndOfLine);
    }

    private FileEndOfLine findEndOfLine() {
        long offset = this.offset;
        for (; offset < addressMax; offset++) {
            byte b = UNSAFE.getByte(offset);

            if (b == CR) {
                if (offset + 1 < addressMax) {
                    b = UNSAFE.getByte(offset + 1);
                    if (b == LF) {
                        return FileEndOfLine.WIN;
                    } else {
                        return FileEndOfLine.MAC;
                    }
                } else {
                    return FileEndOfLine.MAC;
                }
            }
            if (b == LF) {
                return FileEndOfLine.UNIX;
            }
        }
        return FileEndOfLine.NONE;
    }

    public CsvLine line() {

        long[] cols = new long[64];
        boolean quoted = false;
        long offset = this.offset;
        int i = 0;
        for (; offset < addressMax; offset++) {
            byte b = UNSAFE.getByte(offset);

            if (QUOTE == b) {
                quoted = !quoted;
            }

            if (COMMA == b && !quoted) {
                cols[i++] = offset - 1;
            }

            if (CR == b) {
                if (FileEndOfLine.MAC == this.fileEndOfLine) {
                    cols[i++] = offset - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = offset;
                    return csvLine;
                } else if (FileEndOfLine.WIN == this.fileEndOfLine) {
                    cols[i++] = offset - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = offset + 2;
                    return csvLine;
                } else {

                }
            }

            if (LF == b) {
                if (FileEndOfLine.UNIX == this.fileEndOfLine) {
                    cols[i++] = offset - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = offset + 1;
                    return csvLine;
                } else {
                    throw new IllegalStateException();
                }
            }


        }

        return null;
    }

    @Override
    public void close() throws Exception {
        Buffers.releaseDirectByteBuffer(buffer);
    }


}
