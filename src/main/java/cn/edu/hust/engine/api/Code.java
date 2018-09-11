/*
 *	定义ClassFile的Code属性类
 *
 *	1.public int getMaxStack(): max_stack
 *	2.public int geMaxLocals(): max_locals
 *	3.public byte[] getCode(): code[]
 *	4.public void print(): 输出代码
 *	5.public ExceptionTable[] getExceptionTable(): 异常表
 *	6.public Map<String, Attribute> getAttributeMap(): 属性名字对象映射
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 */

package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

import java.util.Map;

/*
 *	Code_attribute {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u2 max_stack;
 *		u2 max_locals;
 *		u4 code_length;
 *		u1 code[code_length];
 *		u2 exception_table_length;
 *		{	u2 start_pc;
 *			u2 end_pc;
 *			u2 handler_pc;
 *			u2 catch_type;
 *		} exception_table[exception_table_length];
 * 		u2 attributes_count;
 * 		attribute_info attributes[attributes_count];
 *	}
 */
public class Code extends Attribute {
    private int maxStack;
    private int maxLocals;
    private int codeZonOffset;
    private int exceptionTableZoneOffset;
    private int attributesZoneOffset;

    public Code( byte[] data, int[] pool, int offset ) {
        super(data, pool, offset);
        maxStack = Bytes.toInt(data, offset + 6);
        maxLocals = Bytes.toInt(data, offset + 8);
        codeZonOffset = offset + 10;
        int code_length = Bytes.toInt(data, offset + 10, 4);
        exceptionTableZoneOffset = offset + code_length + 14;
        int exception_table_length = Bytes.toInt(data, offset + code_length + 14);
        // offset是指向code属性的属性值其中可以有line_number_table_attribute
        attributesZoneOffset = offset + code_length + exception_table_length * 8 + 16;
    }

    /**
     * @return max_stack
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * @return max_locals
     */
    public int geMaxLocals() {
        return maxLocals;
    }

    /**
     * @return code[]
     */
    public byte[] getCode() {
        int    code_length = Bytes.toInt(data, codeZonOffset, 4);
        byte[] code        = new byte[code_length];
        for (int p = codeZonOffset + 4, i = 0; i < code_length; i++) {
            code[i] = data[p + i];
        }
        return code;
    }

    /**
     * 输出代码
     */
    public void print() {
        byte[] code = getCode();
        int    l    = code.length;
        System.out.printf("     Code:\n");
        System.out.printf("       Stack=%d, Locals=%d\n", maxStack, maxLocals);
        for (int i = 0; i < l / 16 + 1; i++) {
            System.out.print("      ");
            for (int j = 0; j < 16; j++) {
                int k = i * 16 + j;
                if (k < l) {
                    System.out.printf(" %02X", code[k]);
                }
            }
            System.out.println();
        }
    }

    /**
     * @return 异常表
     */
    public ExceptionTable[] getExceptionTable() {
        int              exceptionTableLength = Bytes.toInt(data, exceptionTableZoneOffset);
        ExceptionTable[] exceptionTable       = new ExceptionTable[exceptionTableLength];
        for (int p = exceptionTableLength + 2, i = 0; i < exceptionTableLength; p += 8, i++) {
            int startPc   = Bytes.toInt(data, p);
            int endPc     = Bytes.toInt(data, p + 2);
            int handlerPc = Bytes.toInt(data, p + 4);
            int catchType = Bytes.toInt(data, p + 6);
            exceptionTable[i] = new ExceptionTable(startPc, endPc, handlerPc, catchType);
        }
        return exceptionTable;
    }

    /**
     * @return 属性名字对象映射
     */
    public Map<String, Attribute> getAttributeMap() {
        return ClassFile.getAttributesMap(data, pool, attributesZoneOffset);
    }
}