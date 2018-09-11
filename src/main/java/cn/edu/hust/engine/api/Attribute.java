package cn.edu.hust.engine.api;



import cn.edu.hust.engine.utils.Bytes;


/*
 *	attribute_info {
 *		u2 attribute_name_index;
 *		u4 attribute_length;
 *		u1 info[attribute_length];
 *	}
 */
public class Attribute {
    protected int    attributeNameIndex;
    protected int    attributeLength;
    protected int    attributeOffset;
    protected byte[] data;
    protected int[]  pool;

    public Attribute( byte[] data, int[] pool, int attributeOffset ) {
        // offset目前指向属性开始的位置(吃掉属性个数的位置)
        this.data = data;
        this.pool = pool;
        attributeNameIndex = Bytes.toInt(data, attributeOffset);
        attributeLength = Bytes.toInt(data, attributeOffset + 2, 4);
        // 指向真正的属性内容的位置(info的位置，例如：ConstantValue就是u2 constant_value_index的位置)
        this.attributeOffset = attributeOffset + 2 + 4;
    }

    public String getName() {
        if (attributeNameIndex > 0) {
            int attributeNameOffset = pool[attributeNameIndex];
            return new String(data, attributeNameOffset + 1 + 2, attributeNameOffset + 1);
        }
        return "";
    }

    public int getLength() {
        return attributeLength;
    }

    public static Attribute getInstance( String n, byte[] d, int[] p, int o ) {
        Attribute instance;
        if (n.equals("Deprecated"))
            instance = new Deprecated(d, p, o);
        else if (n.equals("ConstantValue"))
            instance = new ConstantValue(d, p, o);
        else if (n.equals("Exceptions"))
            instance = new Exceptions(d, p, o);
        else if (n.equals("Code"))
            instance = new Code(d, p, o);
        else if (n.equals("LineNumberTable"))
            instance = new LineNumberTable(d, p, o);
        else if (n.equals("LocalVariableTable"))
            instance = new LocalVariableTable(d, p, o);
        else if (n.equals("SourceFile"))
            instance = new SourceFile(d, p, o);
        else if (n.equals("InnerClasses"))
            instance = new InnerClasses(d, p, o);
        else if (n.equals("Synthetic"))
            instance = new Synthetic(d, p, o);
        else {
            instance = new Attribute(d, p, o);
        }
        return instance;
    }

}
