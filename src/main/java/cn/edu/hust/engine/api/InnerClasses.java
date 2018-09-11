package cn.edu.hust.engine.api;

import cn.edu.hust.engine.utils.Bytes;

public class InnerClasses extends Attribute {
    private Class[] classes;


    public InnerClasses( byte[] data, int[] pool, int attributeOffset ) {
        super(data, pool, attributeOffset);
        this.classes = buildClasses();
    }

    private Class[] buildClasses() {
        int     numberOfClass           = Bytes.toInt(data, attributeOffset);
        Class[] classes                 = new Class[numberOfClass];
        String  innerClazz              = "";
        String  outerClazz              = "";
        String  clazzName               = "";
        String  accessFlags             = "";
        int     innerClazzInfoIndex     = 0;
        int     innerClazzInfoUtf8Index = 0;
        int     outerClazzInfoIndex     = 0;
        int     outerClazzInfoUtf8Index = 0;
        int     innerClazzNameUtf8Index = 0;
        int     innerClazzAccessFlag    = 0;
        for (int i = attributeOffset + 2, j = 0; j < numberOfClass; j++, i += 8) {
            innerClazzInfoIndex = Bytes.toInt(data, i);
            outerClazzInfoIndex = Bytes.toInt(data, i + 2);
            innerClazzNameUtf8Index = Bytes.toInt(data, i + 4);

            if (innerClazzInfoIndex > 0) {
                innerClazzInfoUtf8Index = Bytes.toInt(data, pool[innerClazzInfoIndex] + 1);
                if (innerClazzInfoUtf8Index > 0) {
                    innerClazz = new String(data, innerClazzInfoUtf8Index + 3, Bytes.toInt(data, innerClazzInfoUtf8Index + 1));
                }
            }
            if (outerClazzInfoIndex > 0) {
                outerClazzInfoUtf8Index = Bytes.toInt(data, pool[outerClazzInfoIndex] + 1);
                if (outerClazzInfoUtf8Index > 0) {
                    outerClazz = new String(data, outerClazzInfoUtf8Index + 3, Bytes.toInt(data, outerClazzInfoUtf8Index + 1));
                }
            }
            if (innerClazzNameUtf8Index > 0) {
                clazzName = new String(data, innerClazzNameUtf8Index + 3, Bytes.toInt(data, innerClazzNameUtf8Index + 1));
            }

            accessFlags = ClassFile.getAccessFlagSet(Bytes.toInt(data, i + 6), Flags.classAccessFlags);

            classes[j] = new Class(innerClazz, outerClazz, clazzName, accessFlags);
        }

        return classes;
    }

    public Class[] getClasses(){
        return this.classes;
    }
}
