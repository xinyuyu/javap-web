/*
 *	定义ClassFile的FieldBase类(供ClassFile的Field类与Method类继承用)
 *
 *	1.public abstract String getAccessFlags(): 字符串格式的access_flags
 *	2.public String getName(): 名字
 *	3.public String getDescriptor(): 描述
 *	4.public Map<String, Attribute> getAttributeMap(): 属性名字对象映射
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 */

package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

import java.util.Map;

/**
 *	field_info {
 *		u2 access_flags;
 * 		u2 name_index;
 * 		u2 descriptor_index;
 * 		attributes_zone {
 * 			u2 attributes_count;
 * 			attribute_info attributes[attributes_count];
 * 		}
 *	}
 */
public abstract class FieldBase {
	protected byte[] data;
	protected int[] pool;
	protected int access_flags, name_index, descriptor_index, attributes_zone_offset;

	public FieldBase( byte[] data, int[] pool, int offset) {
		this.data = data;
		this.pool = pool;
		access_flags = Bytes.toInt(data, offset);
		name_index = Bytes.toInt(data, offset + 2);
		descriptor_index = Bytes.toInt(data, offset + 4);
		// 指向attribute的开始位置，即attribute_count的位置
		attributes_zone_offset = offset + 6;
	}

	/**
	 * @return 字符串格式的access_flags
	 */
	public abstract String getAccessFlags();

	/**
	 * @return 名字
	 */
	public String getName() {
		String name = "";
		if (name_index > 0) {
			int p = pool[name_index];
			name = new String(data, p + 3, Bytes.toInt(data, p + 1));
		}
		return name;
	}

	/**
	 * @return 描述
	 */
	public String getDescriptor() {
		String descriptor = "";
		if (descriptor_index > 0) {
			int p = pool[descriptor_index];
			descriptor = new String(data, p + 3, Bytes.toInt(data, p + 1));
		}
		return descriptor;
	}

	/**
	 * @return 属性名字对象映射
	 */
	public Map<String, Attribute> getAttributeMap() {
		return ClassFile.getAttributesMap(data, pool, attributes_zone_offset);
	}
}