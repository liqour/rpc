package com.test.rpc.util;

public class ByteUtil {

	/**
	 * 
	 * @Description: 连接两个byte数组 
	 * @Title: unitByteArray 
	 * @param @param b1
	 * @param @param b2
	 * @param @return    设定文件 
	 * @return byte[]    返回类型 
	 * @throws 
	 * @author liquor
	 */
	public static byte[] unitByteArray(byte[] b1,byte[] b2){
		byte[] b = new byte[b1.length+b2.length];
		System.arraycopy(b1, 0, b, 0, b1.length);
		System.arraycopy(b2, 0, b, b1.length, b2.length);
		return b;
	}
	
}
