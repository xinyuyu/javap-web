/*
 *	字节数组到整数(无符号整数)、浮点数、长整数(无符号长整数)、双精度数的转换
 *
 *	1.public static int toInt(byte[] b, int i, int l): 从b的第i个元素处取出连续l字节转为整数
 * 	2.public static int toInt(byte b): 将byte转为无符号整数
 * 	3.public static int toInt(byte[] b): 从b的开始处取出连续2个字节转为无符号整数
 * 	4.public static int toInt(byte[] b, int i): 从b的第i个元素处取出连续2字节转为无符号整数
 *
 *	5.public static float toFloat(byte[] b, int i): 从b的第i个元素处取出连续4字节转为浮点数
 *
 * 	6.public static long toLong(byte[] b, int i, int l): 从b的第i个元素处取出连续l字节转为长整数
 * 	7.public static long toLong(byte[] b): 从b的开始处取出连续4个字节转为无符号长整数
 * 	8.public static long toLong(byte[] b, int i): 从b的第i个元素处取出连续4字节转为无符号长整数
 *
 *	9.public static double toDouble(byte[] b, int i): 从b的第i个元素处取出连续8字节转为双精度数
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 *
 */

package cn.edu.hust.engine.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Bytes {
	/**
	 * 从byte[]数组b的第i个元素处取出连续l字节并转为整数
	 */
	public static int toInt(byte[] b, int i, int l) {
		int v = 0;
		for (int p = 0; p < l; p++) {
			int shift = (l - p - 1) * 8;
			v += (b[i + p] & 0x000000FF) << shift;
		}
		return v;
	}

	/**
	 * 将1字节byte转为无符号整数
	 */
	public static int toInt(byte b) {
		return b & 0x000000FF;
	}

	/**
	 * 从byte[]数组b的开始处取出连续2个字节并转为无符号整数
	 */
	public static int toInt(byte[] b) {
		return ((b[0] & 0x000000FF) << 8) + (b[1] & 0x000000FF);
	}

	/**
	 * 从byte[]数组b的第i个元素处取出连续2字节并转为无符号整数
	 */
	public static int toInt(byte[] b, int i) {
		return ((b[i] & 0x000000FF) << 8) + (b[i + 1] & 0x000000FF);
	}

	/**
	 * 从byte[]数组b的第i个元素处取出连续4字节并转为浮点数
	 */
	public static float toFloat(byte[] b, int i) throws Exception {
		DataInputStream in =new DataInputStream(new ByteArrayInputStream(b, i, 4));
		try {
			return in.readFloat();
		} finally {
			in.close();
		}
	}

	/**
	 * 从byte[]数组b的第i个元素处取出连续l字节并转为长整数
	 */
	public static long toLong(byte[] b, int i, int l) {
		long v = 0;
		for (int p = 0; p < l; p++) {
			int shift = (l - p - 1) * 8;
			v += (b[i + p] & 0x00000000000000FF) << shift;
		}
		return v;
	}

	/**
	 * 从byte[]数组b的开始处取出连续4个字节并转为无符号长整数
	 */
	public static long toLong(byte[] b) {
		return toLong(b, 0, 4);
	}

	/**
	 * 从byte[]数组b的第i个元素处取出连续4字节并转为无符号长整数
	 */
	public static long toLong(byte[] b, int i) {
		return toLong(b, i, 4);
	}

	/**
	 * 从byte[]数组b的第i个元素处取出连续8字节并转为双精度数
	 */
	public static double toDouble(byte[] b, int i) throws Exception {
		DataInputStream in =new DataInputStream(new ByteArrayInputStream(b, i, 8));
		try {
			return in.readDouble();
		} finally {
			in.close();
		}
	}
}