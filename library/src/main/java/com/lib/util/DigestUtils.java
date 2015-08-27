package com.lib.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class DigestUtils {

	private static String md5(String paramString) {
		MessageDigest localMessageDigest = null;
		StringBuffer localStringBuffer = new StringBuffer();
		try {
			localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.reset();
			localMessageDigest.update(paramString.getBytes("UTF-8"));
			byte[] arrayOfByte = localMessageDigest.digest();
		
			int i = 0;
			if (i >= arrayOfByte.length)
				return localStringBuffer.toString();

			for (int j = 0; j < arrayOfByte.length; j++) {

				if (Integer.toHexString(0xFF & arrayOfByte[i]).length() == 1) {
					localStringBuffer.append("0").append(
							Integer.toHexString(0xFF & arrayOfByte[i]));
				} else {
					localStringBuffer.append(Integer
							.toHexString(0xFF & arrayOfByte[j]));
				}
			}
		} catch (Exception exception) {

		}
		return localStringBuffer.toString();
	}

	public static Map<String, String> getCrcMap() {
		Map<String, String> localHashMap = new HashMap<String, String>();
		String str1 = Long.toString(System.currentTimeMillis());
		localHashMap.put("ts", str1);
		String str2 = md5("HUIWEN_MOBILE" + str1);
		localHashMap.put("crc", str2.substring(1, 6) + str2.substring(8, 11)
				+ str2.substring(12, 14));
		return localHashMap;
	}
}
