package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

/*
 *	attribute_info {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u1 info[attribute_length];
 *	}
 */
public class ConstantValue extends Attribute {

    public ConstantValue( byte[] data, int[] pool, int attributeOffset ) {
        super(data, pool, attributeOffset);
    }

    public String getConstantValue() {
        String v                  = "";
        int    constantValueIndex = Bytes.toInt(data, attributeOffset);
        if (constantValueIndex > 0) {
            int constantValueStartOffset = pool[constantValueIndex];
            try {
                switch (data[constantValueStartOffset]) {
                    case Tag.CONSTANT_INTEGER:
                        int h2 = Bytes.toInt(data, constantValueStartOffset + 1) << 16;
                        int l2 = Bytes.toInt(data, constantValueStartOffset + 3);
                        v = String.valueOf(h2 + l2);
                        break;
                    case Tag.CONSTANT_FLOAT:
                        v = String.valueOf(Bytes.toFloat(data, constantValueStartOffset + 1)) + "f";
                        break;
                    case Tag.CONSTANT_LONG:
                        long lh2 = Bytes.toLong(data, constantValueStartOffset + 1) << 32;
                        long ll2 = Bytes.toLong(data, constantValueStartOffset + 5);
                        v = String.valueOf(lh2 + ll2 + "l");
                        break;
                    case Tag.CONSTANT_DOUBLE:
                        v = String.valueOf(Bytes.toDouble(data, constantValueStartOffset + 1)) + "d";
                        break;
                }
            } catch (Exception e) {

            }
        }
        return v;
    }
}
