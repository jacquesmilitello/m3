package io.m3.util.hash;

public abstract class HashReader {

	public abstract long length();

	public abstract long getLong();

	public abstract int getInt();

	public abstract long getByte();

}