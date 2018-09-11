package cn.edu.hust.engine.api;

import cn.edu.hust.engine.utils.Bytes;

/*
 *	Exceptions_attribute {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u2 number_of_exceptions;
 *		u2 exception_index_table[number_of_exceptions];
 *	}
 */
public class Exceptions extends Attribute {
    public Exceptions( byte[] data, int[] pool, int attributeOffset ) {
        super(data, pool, attributeOffset);
    }

    public int[] getExceptionIndexTable() {
        int   numberOfExceptions  = Bytes.toInt(data, attributeOffset + 2, 4);
        int[] exceptionIndexTable = new int[numberOfExceptions];
        for (int p = attributeOffset + 2, i = 0; i < numberOfExceptions; p += 2, i++){
            exceptionIndexTable[i] = Bytes.toInt(data, p);
        }
        return exceptionIndexTable;
    }

}
