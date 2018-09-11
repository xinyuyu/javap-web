package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassFile结构定义:
 * <p>
 * 若用u1,u2,u4,u8分别代表1,2,4,8字节无符号整数,则ClassFile(可形式上)定义如下
 * <p>
 * cp_info {
 * u1 tag;
 * u1 info[];
 * }
 * <p>
 * field_info {
 * u2 access_flags;
 * u2 name_index;
 * u2 descriptor_index;
 * u2 attributes_count;
 * attribute_info attributes[attributes_count];
 * }
 * <p>
 * method_info {
 * u2 access_flags;
 * u2 name_index;
 * u2 descriptor_index;
 * u2 attributes_count;
 * attribute_info attributes[attributes_count];
 * }
 * <p>
 * attribute_info {
 * u2 attribute_name_index;
 * u4 attribute_length;
 * u1 info[attribute_length];
 * }
 * <p>
 * ClassFile {
 * u4 magic;
 * u2 minor_version;
 * u2 major_version;
 * constant_pool_zone {
 * u2 constant_pool_count;
 * cp_info constant_pool[constant_pool_count-1];
 * }
 * u2 access_flags;
 * u2 this_class;
 * u2 super_class;
 * interfaces_zone {
 * u2 interfaces_count;
 * u2 interfaces[interfaces_count];
 * }
 * fields_zone {
 * u2 fields_count;
 * field_info fields[fields_count];
 * }
 * methods_zone {
 * u2 methods_count;
 * method_info methods[methods_count];
 * }
 * attributes_zone {
 * u2 attributes_count;
 * attribute_info attributes[attributes_count];
 * }
 * }
 */

public class ClassFile {

    private static final int MAGIC_START_OFFSET         = 0;
    private static final int MINOR_VERSION_START_OFFSET = 4;
    private static final int MAJOR_VERSION_START_OFFSET = 6;
    private static final int CONSTANT_POOL_SIZE_OFFSET  = 8;
    private static final int CONSTANT_POOL_START_OFFSET = 10;

    /**
     * 从class文件转的byte数组中读取前4个字节转换成long即魔数
     *
     * @param data class文件转的数组,下通
     *
     * @return 魔数
     */
    public static long getMagic( byte[] data ) {
        return Bytes.toLong(data, MAGIC_START_OFFSET);
    }
    /**
     * 从class文件转的byte数组中从第4个字节开始（前4个是魔数），读取两个字节为次版本号
     *
     * @param data
     *
     * @return 次版本号
     */
    public static int getMinorVersion( byte[] data ) {
        return Bytes.toInt(data, MINOR_VERSION_START_OFFSET);
    }

    /**
     * @param data
     *
     * @return
     *
     * @code 参见getMinorVersion
     */
    public static int getMajorVersion( byte[] data ) {
        return Bytes.toInt(data, MAJOR_VERSION_START_OFFSET);
    }

    public static int getConstantPoolSize( byte[] data ) {
        return Bytes.toInt(data, CONSTANT_POOL_SIZE_OFFSET);
    }

    public static int[] getConstantPool( byte[] data ) {
        // 常量池始于文件的第11字节处,其元素数个数(用U2格式)存放在文件的第9-10字节处,共为c-1个
        int constantPoolSize = getConstantPoolSize(data);
        int tag              = data[CONSTANT_POOL_START_OFFSET];
        // 常量池里的项在data数组中的索引位置
        int constantOffset = CONSTANT_POOL_START_OFFSET;
        // 数组的值是常量在data数组中的offset
        int[] pool = new int[constantPoolSize];
        int   i    = 1;
        while (i < constantPoolSize) {
            pool[i] = constantOffset;
            switch (tag) {
                case Tag.CONSTANT_UTF8:
                    // 吃掉一个CONSTANT_UTF8类型的常量，索引位置指向下一个常量的开始
                    // 以下每个case都类似
                    // poolIndex加上utf8字符串长度再加3（tag+length）
                    constantOffset += Bytes.toInt(data, constantOffset + 1) + 3;
                    break;
                case Tag.CONSTANT_STRING:
                case Tag.CONSTANT_CLASS:
                case Tag.CONSTANT_METHOD_TYPE:// TODO 暂时未分析
                    constantOffset += 1 + 2;
                    break;
                case Tag.CONSTANT_METHOD_HANDLE:// TODO 暂时未分析
                    constantOffset += 1 + 1 + 2;
                    break;
                case Tag.CONSTANT_INTEGER:
                case Tag.CONSTANT_FLOAT:
                case Tag.CONSTANT_FIELD_REF:
                case Tag.CONSTANT_METHOD_REF:
                case Tag.CONSTANT_INTERFACE_METHOD_REF:
                case Tag.CONSTANT_NAME_AND_TYPE:
                case Tag.CONSTANT_INVOKE_DYNAMIC:// TODO 暂时未分析
                    constantOffset += 1 + 2 + 2;
                    break;
                case Tag.CONSTANT_LONG:
                case Tag.CONSTANT_DOUBLE:
                    constantOffset += 1 + 4 + 4;
                    // double和long会占用两个常量池slot,故跳过一个常量池项
                    i++;
                    break;
            }
            // 获取下一个常量的tag
            tag = data[constantOffset];
            i++;
        }
        // 这里将常量的个数存放在常量池第一个位置出，因为这个位置没有存放常量项
        pool[0] = constantOffset;
        // pool常量池记录的是某个常量在data字节数组里的offset
        return pool;
    }

    public static int getAccessFlagsToInt(byte [] data, int [] pool){
        return Bytes.toInt(data, pool[0]);
    }
    /**
     * access_flags是16位通过不同位上是否有1来决定是否有权限的
     * 故通过与操作可以得到分别有什么权限
     *
     * @param access_flags
     * @param keyValues
     *
     * @return
     */
    public static String getAccessFlagSet( int access_flags, KeyValue[] keyValues) {
        String     accessFlags    = "";
        KeyValue[] values         = keyValues;
        for (KeyValue value : values) {
            if ((value.value & access_flags) == value.value) {
                if (!accessFlags.isEmpty())
                    accessFlags += " ";
                accessFlags += value.key;
            }
        }
        return accessFlags;
    }


    /**
     * pool[0]存储的是常量池的总长度即pool[0]后就是access_flags的offset
     * 再往后顺延两个就是this_class的offset
     * 再往后顺延两个就是super_class的offset
     * 再往后顺延两个就是interface_count
     *
     * @param data
     * @param pool 常量池里存的是每个类型常量开始的offset
     *
     * @return
     */
    public static int getThisClassIndexInConstantPool( byte[] data, int[] pool ) {
        // pool[0] + 2 是this_class在data里的offset
        // 其值是常量池一个索引指向一个CONSTANT_CLASS类型
        return Bytes.toInt(data, pool[0] + 2);
    }

    /**
     * getThisClassOffset方法返回this_class在data数组中的offset，该值又是一个指针指向常量池
     * 中的一项该项是constant_utf8,可从中获取类名的字面量
     *
     * @param data
     * @param pool
     *
     * @return
     */
    public static String getThisClassName( byte[] data, int[] pool ) {
        // index表示在常量池中的索引
        // 从data数组中获取this_class的索引,即pool的数组下标
        int thisClassIndex = getThisClassIndexInConstantPool(data, pool);
        // offset表示在data数组中的偏移量
        int thisClassOffset = pool[thisClassIndex];
        int utf8Index       = Bytes.toInt(data, thisClassOffset + 1);
        if (utf8Index > 0) {
            int classNameStartOffset = pool[utf8Index];
            return new String(data, classNameStartOffset + 3, Bytes.toInt(data, classNameStartOffset + 1));
        }
        return "";
    }


    public static int getSuperClassIndexInConstantPool( byte[] data, int[] pool ) {
        return Bytes.toInt(data, pool[0] + 4);
    }

    /**
     * @param data
     * @param pool
     *
     * @return 例：java/lang/Object
     */
    public static String getSuperClassName( byte[] data, int[] pool ) {
        int superClassIndex  = getSuperClassIndexInConstantPool(data, pool);
        int superClassOffset = pool[superClassIndex];
        int utf8Index        = Bytes.toInt(data, superClassOffset + 1);
        if (utf8Index > 0) {
            int superClassStartOffset = pool[utf8Index];
            return new String(data, superClassStartOffset + 3, Bytes.toInt(data, superClassStartOffset + 1));
        }
        return "";
    }

    /**
     * @param data
     * @param pool
     *
     * @return interface_count开始的offset
     */
    public static int getInterfaceZoneOffset( byte[] data, int[] pool ) {
        // 每个2分别代表吃掉access_flags,this_class,super_class
        return pool[0] + 2 + 2 + 2;
    }

    /**
     * pool[0]表示常量池结束的offset
     * 向后移6表示super_class结束的offset
     * 向后移2表示interface_count结束的offset
     * 每个interface占用2个字节，所以乘以2
     *
     * @param data
     * @param pool
     *
     * @return fields_count开始的offset
     */
    public static int getFieldsZoneOffset( byte[] data, int[] pool ) {
        // 2代表吃掉interface_count
        // 每个interface占用2个字节故再加上一个乘法
        return getInterfaceZoneOffset(data, pool) + 2 + Bytes.toInt(data, getInterfaceZoneOffset(data, pool)) * 2;
    }

    public static int getMethodsZoneOffset( byte[] data, int[] pool ) {
        /*
         * field_info {
         * 		u2 access_flags;
         * 		u2 name_index;
         * 		u2 descriptor_index;
         * 		u2 attributes_count;
         * 		attribute_info attributes[attributes_count];
         * }
         * attribute_info {
         * 		u2 attribute_name_index;
         * 		u4 attribute_length;
         * 		u1 info[attribute_length];
         * }
         */
        int fieldsZoneIndex   = getFieldsZoneOffset(data, pool);
        int fieldsCount       = Bytes.toInt(data, fieldsZoneIndex);
        int methodsZoneOffset = fieldsZoneIndex + 2;
        for (int i = 0; i < fieldsCount; i++) {
            // 分别吃掉field_info结构里的access_flags,name_index,descriptor_index,attributes_count
            methodsZoneOffset += 2 + 2 + 2 + 2;
            int fieldsAttributesCount = Bytes.toInt(data, methodsZoneOffset - 2);
            for (int j = 0; j < fieldsAttributesCount; j++) {
                methodsZoneOffset += Bytes.toInt(data, methodsZoneOffset + 2, 4) + 2 + 4;
            }
        }
        return methodsZoneOffset;
    }

    public static int getAttributesZoneOffset( byte[] data, int[] pool ) {
        /*
         * method_info {
         * 		u2 access_flags;
         * 		u2 name_index;
         * 		u2 descriptor_index;
         * 		u2 attributes_count;
         * 		attribute_info attributes[attributes_count];
         * }
         * attribute_info {
         * 		u2 attribute_name_index;
         * 		u4 attribute_length;
         * 		u1 info[attribute_length];
         * }
         */
        int methodsZoneOffset   = getMethodsZoneOffset(data, pool);
        int methodsCount        = Bytes.toInt(data, methodsZoneOffset);
        int attributeZoneOffset = methodsZoneOffset + 2;
        for (int i = 0; i < methodsCount; i++) {
            attributeZoneOffset += 2 + 2 + 2 + 2;
            int attributesCount = Bytes.toInt(data, attributeZoneOffset - 2);
            for (int j = 0; j < attributesCount; j++) {
                attributeZoneOffset += Bytes.toInt(data, attributeZoneOffset + 2, 4) + 2 + 4;
            }
        }
        return attributeZoneOffset;
    }


    public static void printConstantPool( byte[] data, int[] pool ) {
        System.out.println("==========constant pool===========");
        int poolSize = pool.length;
        int tag      = 0;
        int offset   = 0;
        System.out.println("size is : " + poolSize);
        System.out.printf("%4s|%5s|%2s|%s\n", "oft", "idx", "tg", "content");
        System.out.println("---------------------");
        for (int index = 1; index < poolSize; index++) {
            offset = pool[index];
            tag = data[offset];
            switch (tag) {
                case Tag.CONSTANT_UTF8:
                    String utf8InfoStr = new String(data, offset + 3, Bytes.toInt(data, offset + 1));
                    // data中从0开始计算
                    System.out.printf("%04d|#%04d|%02d|%s\n", offset, index, tag, utf8InfoStr);
                    break;
                case Tag.CONSTANT_CLASS:
                case Tag.CONSTANT_STRING:
                case Tag.CONSTANT_METHOD_TYPE:
                    int indexInConstant = Bytes.toInt(data, offset + 1);
                    System.out.printf("%04d|#%04d|%02d|#%04d\n", offset, index, tag, indexInConstant);
                    break;
                case Tag.CONSTANT_METHOD_HANDLE:
                    int kind = data[offset + 1];
                    int reference_index = Bytes.toInt(data, offset + 2);
                    System.out.printf("%04d|#%04d|%02d|%02d|#%04d\n", offset, index, tag, kind, reference_index);
                    break;
                case Tag.CONSTANT_INTEGER:
                    int h2 = Bytes.toInt(data, offset + 1);
                    int l2 = Bytes.toInt(data, offset + 3);
                    int v4 = (h2 << 16) + l2;
                    System.out.printf("%04d|#%04d|%02d|%d(h2:%04d, l2:%04d)\n", offset, index, tag, v4, h2, l2);
                    break;
                case Tag.CONSTANT_FLOAT:
                    h2 = Bytes.toInt(data, offset + 1);
                    l2 = Bytes.toInt(data, offset + 3);
                    float f4 = 0;
                    try {
                        f4 = Bytes.toFloat(data, offset + 1);
                    } catch (Exception e) {

                    }
                    System.out.printf("%04d|#%04d|%02d|%ff(h2:%04d,l2:%04d)\n", offset, index, tag, f4, h2, l2);
                case Tag.CONSTANT_FIELD_REF:
                case Tag.CONSTANT_METHOD_REF:
                case Tag.CONSTANT_INTERFACE_METHOD_REF:
                case Tag.CONSTANT_NAME_AND_TYPE:
                case Tag.CONSTANT_INVOKE_DYNAMIC:
                    long index1 = Bytes.toInt(data, offset + 1);
                    long index2 = Bytes.toInt(data, offset + 3);
                    System.out.printf("%04d|#%04d|%02d|#%04d,#%04d\n", offset, index, tag, index1, index2);
                    break;
                case Tag.CONSTANT_LONG:
                    long h4 = Bytes.toLong(data, offset + 1, 4);
                    long l4 = Bytes.toLong(data, offset + 5, 4);
                    long v8 = (h4 << 32) + l4;
                    System.out.printf("%04d|#%04d|%02d|%dl(h4:%08d,l4:%08d)\n", offset, index, tag, v8, h4, l4);
                    // 吃掉一个空白的slot
                    index++;
                    break;
                case Tag.CONSTANT_DOUBLE:
                    h4 = Bytes.toLong(data, offset + 1, 4);
                    l4 = Bytes.toLong(data, offset + 5, 4);
                    double d8 = 0;
                    try {
                        d8 = Bytes.toDouble(data, offset + 1);
                    } catch (Exception e) {
                    }
                    System.out.printf("%04d|#%04d|%02d|%fd(h4:%d,l4:%d)\n", d8, index, tag, d8, h4, l4);
                    index++;
                    break;
            }

        }
    }

    /**
     * @return 属性名字与属性对象映射
     */
    public static Map<String, Attribute> getAttributesMap( byte[] data, int[] pool, int zoneOffset) {
        Map<String, Attribute> attributesMap = new HashMap<>();
        Map<String, Integer> offsetsMap = getAttributesOffsetMap(data, pool, zoneOffset);
        for (String name: offsetsMap.keySet()) {
            attributesMap.put(name, Attribute.getInstance(name, data, pool, offsetsMap.get(name)));
        }
        return attributesMap;
    }
    /**
     * @param zoneOffset-属性区段偏移(实际指向attributes_count处)(下同)
     * @return 属性名字与属性偏移映射
     */
    public static Map<String, Integer> getAttributesOffsetMap(byte[] data, int[] pool, int zoneOffset) {
        Map<String, Integer> offsetsMap = new HashMap<String, Integer>();
        int c = Bytes.toInt(data, zoneOffset);
        for (int i = zoneOffset + 2, j = 0; j < c; i += Bytes.toInt(data, i + 2, 4) + 6, j++) {
            int p = pool[Bytes.toInt(data, i)];
            String name = new String(data, p + 3, Bytes.toInt(data, p + 1));
            offsetsMap.put(name, i);
        }
        return offsetsMap;
    }


    public static void main( String[] args ) throws IOException {
//        FileInputStream stream = new FileInputStream(new File("/Users/yuyu/Desktop/MyFreeDemo/ClassParsers/test/C.class"));
        FileInputStream stream = new FileInputStream(new File("/Users/yuyu/Desktop/space/my/ClassParserRefactor/out/production/ClassParserRefactor/cn/edu/hust/classparser/api/ClassFile.class"));
        byte[]          data   = new byte[1024 * 10];
        stream.read(data);
        long magic    = ClassFile.getMagic(data);
        int  minor    = ClassFile.getMinorVersion(data);
        int  major    = ClassFile.getMajorVersion(data);
        int  poolSize = ClassFile.getConstantPoolSize(data);
//        System.out.println(magic);
//        System.out.println(minor);
//        System.out.println(major);
//        System.out.println(poolSize);


        int[] pool = ClassFile.getConstantPool(data);
//        System.out.println(pool[0]);

//
//        for (int poolItem :
//                pool) {
//            System.out.println(poolItem);
//        }

//        String thisClassName = ClassFile.getThisClassName(data, ClassFile.getConstantPool(data));
//        System.out.println(thisClassName);

//        String superClass = ClassFile.getSuperClassName(data, ClassFile.getConstantPool(data));
//        System.out.println(superClass);

//        String accessFlags = ClassFile.getAccessFlagSet(data, pool);
//        System.out.println(accessFlags);
        ClassFile.printConstantPool(data, pool);

    }
}
