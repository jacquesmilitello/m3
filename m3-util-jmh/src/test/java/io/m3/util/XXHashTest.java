package io.m3.util;

import org.junit.Assert;
import org.junit.Test;
import io.m3.util.hash.Hashing;

import net.openhft.hashing.LongHashFunction;

public class XXHashTest {

	
	@Test
	public void test() {
		
		//compare(12356, "Hello world !");
		//compare(12356, "Hello world !d");
		compare(12356, "Hello world !ddsd kjgdfjkdfg kdfgjkfdgjkl dfgk ldfshgk hdfgkldfhgl dfshg dsfh lsdfgh lsdfgh dsflgmh dfslmg hdfsmgl ");
		
		
	}
	
	public void compare(long seed, String value) {
		Assert.assertEquals(LongHashFunction.xx(seed).hashChars(value), Hashing.xx(seed, value));
	}
	
}
