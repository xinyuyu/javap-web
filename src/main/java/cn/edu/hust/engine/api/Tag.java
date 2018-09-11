package cn.edu.hust.engine.api;

/**
 * 通过Tag区分不同的常量池项
 * tag占u2，用16进制表示
 */
public interface Tag {

    // u1 tag=1; u2 length; u1 byte[length]
    public int CONSTANT_UTF8 = 0x01;

    // u1 tag=3; u4 bytes
    public int CONSTANT_INTEGER = 0x03;

    // u1 tag=4; u4 bytes
    public int CONSTANT_FLOAT = 0x04;

    // u1 tag=5; u4 high_bytes; u4 low_bytes
    public int CONSTANT_LONG = 0x05;

    // u1 tag=6; u4 high_bytes; u4 low_bytes
    public int CONSTANT_DOUBLE = 0x06;

    // u1 tag=7; u2 name_index
    public int CONSTANT_CLASS = 0x07;

    // u1 tag=8; u2 string_index
    public int CONSTANT_STRING = 0x08;

    // u1 tag=9; u2 class_index; u2 name_and_type_index
    // name_type_index的两个索引分别指向字段名和字段类型
    public int CONSTANT_FIELD_REF = 0x09;

    // u1 tag=10; u2 class_index; u2 name_and_type_index
    // name_type_index的两个索引分别指向方法名和方法类型
    public int CONSTANT_METHOD_REF = 0x0A;

    // u1 tag=11; u2 class_index; u2 name_and_type_index
    // name_type_index的两个索引分别指向接口名和接口方法
    public int CONSTANT_INTERFACE_METHOD_REF = 0x0B;

    // u1 tag=12; u2 name_index; u2 descriptor_index
    public int CONSTANT_NAME_AND_TYPE = 0x0C;

    /*以下三个属性好没有研究*/
    //TODO
    // u1 tag=15; u1 reference_kind; u2 reference_index
    public int CONSTANT_METHOD_HANDLE = 0x0F;

    // u1 tag=16; u2 descriptor_index
    public int CONSTANT_METHOD_TYPE = 0x10;

    // u1 tag=18; u2 bootstrap_method_attr_index; u2 bootstrap_method_attr_index
    public int CONSTANT_INVOKE_DYNAMIC = 0x12;

}
