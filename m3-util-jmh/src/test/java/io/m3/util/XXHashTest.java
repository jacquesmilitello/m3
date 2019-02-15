package io.m3.util;

import io.m3.util.hash.Hashing;

import net.openhft.hashing.LongHashFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XXHashTest {

	
	@Test
	void test() {
		
		//compare(12356, "Hello world !");
		//compare(12356, "Hello world !d");
		compare(12356, "Hello world !ddsd kjgdfjkdfg kdfgjkfdgjkl dfgk ldfshgk hdfgkldfhgl dfshg dsfh lsdfgh lsdfgh dsflgmh dfslmg hdfsmgl ");
		
		
	}
	
	void compare(long seed, String value) {
		assertEquals(LongHashFunction.xx(seed).hashChars(value), Hashing.xx(seed, value));
	}
	
}
