package org.zframework.web.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zframework.core.util.Sha1Util;

public class WeiXin {
	public static boolean access(String token,String signature,String timestamp,String nonce) {
		List<String> ss = new ArrayList<String>();
		ss.add(timestamp);
		ss.add(nonce);
		ss.add(token);

		Collections.sort(ss);

		StringBuilder builder = new StringBuilder();
		for(String s : ss) {
			builder.append(s);
		}
		return signature.equalsIgnoreCase(Sha1Util.sha1(builder.toString()));
	}
}
