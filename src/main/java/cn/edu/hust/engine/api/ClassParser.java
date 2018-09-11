package cn.edu.hust.engine.api;


import cn.edu.hust.engine.utils.Bytes;
import lombok.Data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClassParser {

    private byte[]  data;
    private boolean isClassFile;
    private int[]   pool;

    private void init( byte[] data ) {
        this.data = data;
        isClassFile = ClassFile.getMagic(data) == 0xCAFEBABE;
        if (isClassFile) {
            pool = ClassFile.getConstantPool(data);
        }
    }

    private void init( InputStream in ) throws Exception {
        List<Integer> list = new ArrayList<>();
        int           b;
        while ((b = in.read()) != -1) {
            list.add(b);
        }
        byte[] data = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i).byteValue();
        }
        this.init(data);
    }

    public ClassParser( byte[] data ) {
        init(data);
    }

    public ClassParser( InputStream in ) throws Exception {
        this.init(in);
    }

    public ClassParser( String filePath ) throws Exception {
        InputStream in = new FileInputStream(filePath);
        this.init(in);
    }

    public long getMagic() {
        if (isClassFile)
            return ClassFile.getMagic(data);
        return -1l;
    }

    public int getMinorVersion() {
        if (isClassFile)
            return ClassFile.getMinorVersion(data);
        return -1;

    }

    public int getMajorVersion() {
        if (isClassFile)
            return ClassFile.getMajorVersion(data);
        return -1;
    }

    public int[] getConstantPool() {
        if (isClassFile)
            return pool;
        return new int[0];
    }

    public String getAccessFlags() {
        if (isClassFile) {
            int accessFlags = ClassFile.getAccessFlagsToInt(data, pool);
            return ClassFile.getAccessFlagSet(accessFlags, Flags.classAccessFlags);
        }
        return "";

    }

    public boolean isInterface() {
        if (isClassFile)
            return "0x0200".equals(ClassFile.getAccessFlagsToInt(data, pool));
        return false;
    }

    public String getClassName() {
        if (isClassFile)
            return ClassFile.getThisClassName(data, pool);
        return "";
    }

    public String getSuperClassName() {
        if (isClassFile)
            return ClassFile.getSuperClassName(data, pool);
        return "";
    }

    public String getPackagePath() {
        if (isClassFile) {
            String path = "/", name = ClassFile.getThisClassName(data, pool);
            int    i    = name.lastIndexOf("/");
            if (i >= 0) {
                path += name.substring(0, i + 1);
            }
            return path;
        }
        return "";
    }

    public int getInterfacesCount() {
        if (isClassFile) {
            return Bytes.toInt(data, ClassFile.getInterfaceZoneOffset(data, pool));
        }
        return -1;
    }

    public String[] getInterfaceNames() {
        if (isClassFile) {
            int      interfacesCount      = getInterfacesCount();
            int      interfacesZoneOffset = ClassFile.getInterfaceZoneOffset(data, pool);
            String[] interfaceNames       = new String[interfacesCount];
            for (int i = interfacesZoneOffset + 2, j = 0; j < interfacesCount; i += 2, j++) {
                int utf8Index = Bytes.toInt(data, pool[Bytes.toInt(data, i) + 1]);
                interfaceNames[i] = new String(data, pool[utf8Index] + 3, Bytes.toInt(data, pool[utf8Index] + 1));
            }
        }
        return new String[0];
    }

    public int getFieldCount() {
        if (isClassFile)
            return Bytes.toInt(data, ClassFile.getFieldsZoneOffset(data, pool));
        return -1;
    }

    public Field[] getFields() {
        if (isClassFile) {
            int     fieldsZoneOffset = ClassFile.getFieldsZoneOffset(data, pool);// 得到字段区的偏移量（offset指向fields_count位置）
            int     fieldsCount      = Bytes.toInt(data, fieldsZoneOffset);// 得到字段数
            Field[] fields           = new Field[fieldsCount];
            fieldsZoneOffset += 2;// offset吃掉field_count;
            for (int i = 0; i < fieldsCount; i++) {
                fields[i] = new Field(data, pool, fieldsZoneOffset);// offset目前指向真正的field位置
                int attributesCount = Bytes.toInt(data, fieldsZoneOffset + 6);// +6吃掉field_info里的u2 access_flag,u2 name_index,u2 description_index
                fieldsZoneOffset += 8;// offset指向attribute的开始
                for (int j = 0; j < attributesCount; j++) {
                    // offset指向下一个field开始的真正位置 吃掉attribute的长度与定义attribute的6个u2
                    fieldsZoneOffset += Bytes.toInt(data, fieldsZoneOffset + 2, 4) + 6;
                }
            }
            return fields;
        }
        return new Field[0];
    }

    public int getMethodsCount() {
        if (isClassFile)
            return Bytes.toInt(data, ClassFile.getMethodsZoneOffset(data, pool));
        return -1;
    }

    public Method[] getMethods() {
        if (isClassFile) {
            int      methodsZoneOffset = ClassFile.getMethodsZoneOffset(data, pool);
            int      methodsCount      = Bytes.toInt(data, methodsZoneOffset);
            Method[] methods           = new Method[methodsCount];
            methodsZoneOffset += 2;
            for (int i = 0; i < methodsCount; i++) {
                methods[i] = new Method(data, pool, methodsZoneOffset);
                int attributesCount = Bytes.toInt(data, methodsZoneOffset+ 6);
                methodsZoneOffset += 8;
                for (int j = 0; j < attributesCount; j++) {
                    methodsZoneOffset += Bytes.toInt(data, methodsZoneOffset + 2, 4) + 6;
                }
            }
            return methods;
        }
        return new Method[0];
    }

    public int getAttributeCount() {
        if (isClassFile) {
            int attributesZoneOffset = ClassFile.getAttributesZoneOffset(data, pool);
            return Bytes.toInt(data, attributesZoneOffset);
        }
        return -1;
    }


    public void printConstantPool() {
        ClassFile.printConstantPool(data, pool);
    }

    public String createHtmlShow(){


        return "";
    }

}
