/*
 *	定义ClassFile的LineNumberTable属性类
 *
 *	1.public LineTable[] getLineNumberTable(): line_number_table数组
 *	2.public void print(): 输出line_number_table
 *
 *	Copyright (c) 2014.2.21, All rights reserved.
 */

package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

/*
 *	LineNumberTable_attribute {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u2 line_number_table_length;
 *		{	u2 start_pc;
 *			u2 line_number;
 *		} line_number_table[line_number_table_length];
 *	}
 */
public class LineNumberTable extends Attribute {
    public LineNumberTable( byte[] data, int[] pool, int offset ) {
        super(data, pool, offset);
    }

    /**
     * @return line_number_table数组
     */
    public LineTable[] getLineNumberTable() {
        LineTable[] lineNumberTable = new LineTable[Bytes.toInt(data, attributeOffset)];
        for (int p = attributeOffset + 2, i = 0; i < lineNumberTable.length; p += 4, i++) {
            int start_pc    = Bytes.toInt(data, p);
            int line_number = Bytes.toInt(data, p + 2);
            lineNumberTable[i] = new LineTable(line_number, start_pc);
        }
        return lineNumberTable;
    }

    /**
     * 输出line_number_table
     */
    public void print() {
        LineTable[] lineNumberTable = getLineNumberTable();
        if (lineNumberTable.length > 0) {
            System.out.printf("     LineNumberTable:\n");
            for (LineTable line_table : lineNumberTable) {
                int start_pc    = line_table.start_pc;
                int line_number = line_table.line_number;
                System.out.printf("       %s%4d:%4d\n", "Line", start_pc, line_number);
            }
        }
    }
}