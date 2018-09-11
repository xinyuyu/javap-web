/*
 *	定义ClassFile的LocalVariableTable属性类
 *
 *	1.public LocalTable[] getLocalVariableTable(): local_variable_table数组
 *	2.public void print(): 输出local_variable_table
 *
 *	Copyright (c) 2014.2.21, All rights reserved.
 */

package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

/*
 *	LocalVariableTable_attribute {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u2 local_variable_table_length;
 *		{	u2 start_pc;
 *			u2 length;
 *			u2 name_index;
 *			u2 descriptor_index;
 *			u2 index;
 *		} local_variable_table[local_variable_table_length];
 *	}
 */
public class LocalVariableTable extends Attribute {
	public LocalVariableTable( byte[] data, int[] pool, int offset) {
		super(data, pool, offset);
	}

	/**
	 * @return local_variable_table数组
	 */
	public LocalTable[] getLocalVariableTable() {
		LocalTable[] localVariableTable = new LocalTable[Bytes.toInt(data, attributeOffset)];
		for (int p = attributeOffset + 2, i = 0; i < localVariableTable.length; p += 10, i++) {
			int start_pc = Bytes.toInt(data, p);
			int length = Bytes.toInt(data, p + 2);
			int name_index = Bytes.toInt(data, p + 4);
			int descriptor_index = Bytes.toInt(data, p + 6);
			int index = Bytes.toInt(data, p + 8);
			localVariableTable[i] = new LocalTable(start_pc, length, name_index, descriptor_index, index);
		}
		return localVariableTable;
	}

	/**
	 * 输出local_variable_table
	 */
	public void print() {
		LocalTable[] localVariableTable = getLocalVariableTable();
		if (localVariableTable.length > 0) {
			System.out.printf("     LocalVariableTable:\n");
			System.out.printf("      %6s%7s%5s  %-24s %-48s\n", "Start", "Length", "Slot", "Name", "Signature");
			int p, start, length, index, name_index;
			for (LocalTable local_table: localVariableTable) {
				start = local_table.start_pc;
				length = local_table.length;
				index = local_table.index;
				name_index = local_table.name_index;
				String name = "";
				if (name_index > 0) {
					p = pool[name_index];
					name = new String(data, p + 3, Bytes.toInt(data, p + 1));
				}
				String descriptor = "";
				int descriptor_index = local_table.descriptor_index;
				if (descriptor_index > 0) {
					p = pool[descriptor_index];
					descriptor = new String(data, p + 3, Bytes.toInt(data, p + 1));
				}
				System.out.printf("      %6d%7d%5d  %-24s %-48s\n", start, length, index, name, descriptor);
			}
		}
	}
}