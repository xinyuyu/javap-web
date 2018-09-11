/*
 *	定义ClassFile的Method类
 *
 *	1.public String getAccessFlags(): 字符串格式的access_flags
 *	2.public String getMethodSignature(): 方法签名
 *
 *	Copyright (c) 2014.2.21, All rights reserved.
 */

package cn.edu.hust.engine.api;

/**
 *	method_info {
 *		u2 access_flags;
 * 		u2 name_index;
 * 		u2 descriptor_index;
 * 		attributes_zone {
 * 			u2 attributes_count;
 * 			attribute_info attributes[attributes_count];
 * 		}
 *	}
 */
public class Method extends FieldBase {
	public Method( byte[] data, int[] pool, int offset) {
		super(data, pool, offset);
	}

	/**
	 * @return 字符串格式的access_flags
	 */
	public String getAccessFlags() {
		return ClassFile.getAccessFlagSet(access_flags, Flags.methodAccessFlags);
	}

	/**
	 * @return 方法签名
	 */
	public String getMethodSignature() {
		String params = "", retType = "", descriptor = getDescriptor();
		int p = descriptor.lastIndexOf(')');
		/*
		if (p > 0) {
			params = TypesSplit.getTypes(descriptor.substring(1, p));
			retType = TypesSplit.getTypes(descriptor.substring(p + 1));
		}
		*/
		//return getAccessFlags() + " " + retType + " " + getName() + "(" + params + ")";
		return getAccessFlags() + " " + descriptor;
	}
}