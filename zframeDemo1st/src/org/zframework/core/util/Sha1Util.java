package org.zframework.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1Util {
	/**
	 * 计算sha1
	 * @param value
	 * @return
	 */
	public final static String sha1(String value) {
		try {
			return HashUtil.hash(MessageDigest.getInstance("SHA1"), value);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
