package org.zframework.core.util;

import java.security.MessageDigest;

/**
 * 获取Hash值
 * @author zengchao
 *
 */
public class HashUtil {
	private static final char[] LETTERS = "0123456789ABCDEF".toCharArray();
	/**
	 * 
	 * @param digest
	 * @param src
	 * @return
	 */
	public final static String hash(MessageDigest digest,String src) {
		return toHexString(digest.digest(src.getBytes()));
	}

	private final static String toHexString(byte[] bytes) {
		char[] values = new char[bytes.length * 2];
		int i=0;
		for(byte b : bytes) {
			values[i++] = LETTERS[((b & 0xF0) >>> 4)];
			values[i++] = LETTERS[b & 0xF];
		}
		return String.valueOf(values);
	}

}
