/*
 *	定义ClassFile的Field类
 *
 *	1.public String getAccessFlags(): 字符串格式的access_flags
 *	2.public String getFieldSignature(): 字段签名
 *
 *	Copyright (c) 2014.2.21, All rights reserved.
 */

package cn.edu.hust.engine.api;

import java.util.Map;

/**
 *	field_info {
 *		u2 access_flags;
 *		u2 name_index;
 *		u2 descriptor_index;
 *		u2 attributes_count;
 *		attribute_info attributes[attributes_count];
 *	}
 */
public class Field extends FieldBase {
	public Field( byte[] data, int[] pool, int offset) {
		super(data, pool, offset);
	}

	/**
	 * @return 字符串格式的access_flags
	 */
	public String getAccessFlags() {
		return ClassFile.getAccessFlagSet(access_flags, Flags.fieldAccessFlags);
	}

	/**
	 * @return 字段签名(访问信息  类型  名字  = 值)
	 */
	public String getFieldSignature() {
		String descriptor = getDescriptor();
		String fieldSignature = getAccessFlags() + " " + descriptor + " " + getName();
		Map<String, Attribute> attributeMap = getAttributeMap();
		if (attributeMap.containsKey("ConstantValue")) {
			ConstantValue constantValue = (ConstantValue)attributeMap.get("ConstantValue");
			String v = constantValue.getConstantValue();
			if (descriptor.equals("char"))
				v = "'" + (char)Short.parseShort(v) + "'";
			else if (descriptor.equals("boolean")) {
				if (v.equals("1"))
					v = "true";
				else {
					v = "false";
				}
			}
			fieldSignature += " = " + v;
		}
		return fieldSignature + ";";
	}
}