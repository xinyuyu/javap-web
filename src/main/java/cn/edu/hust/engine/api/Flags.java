package cn.edu.hust.engine.api;/*
 *	类、字段及方法访问信息(Access_flags)常量数组定义
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 */


public interface Flags {
	public KeyValue[] classAccessFlags = {
			new KeyValue("public", 0x0001),
			new KeyValue("final", 0x0010),
			new KeyValue("super", 0x0020),
			new KeyValue("abstract", 0x0040),
			new KeyValue("interface", 0x0200),
			new KeyValue("synthetic", 0x1000),
			new KeyValue("annotation", 0x2000),
			new KeyValue("enum", 0x4000)
	};

	public KeyValue[] fieldAccessFlags = {
			new KeyValue("public", 0x0001),
			new KeyValue("private", 0x0002),
			new KeyValue("protected", 0x0004),
			new KeyValue("static", 0x0008),
			new KeyValue("final", 0x0010),
			new KeyValue("volatile", 0x0040),
			new KeyValue("transient", 0x0080),
			new KeyValue("synthetic", 0x1000),
			new KeyValue("enum", 0x4000)
	};

	public KeyValue[] methodAccessFlags = {
			new KeyValue("public", 0x0001),
			new KeyValue("private", 0x0002),
			new KeyValue("protected", 0x0004),
			new KeyValue("static", 0x0008),
			new KeyValue("final", 0x0010),
			new KeyValue("synchronized", 0x0020),
			new KeyValue("volatile", 0x0040),
			new KeyValue("transient", 0x0080),
			new KeyValue("native", 0x0100),
			new KeyValue("synthetic", 0x1000),
			new KeyValue("enum", 0x4000)
	};
}
