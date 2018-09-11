package cn.edu.hust.engine.api;

import cn.edu.hust.engine.utils.Bytes;

public class SourceFile extends Attribute {

    public SourceFile( byte[] data, int[] pool, int attributeOffset ) {
        super(data, pool, attributeOffset);
    }

    public String getSourceFile() {
        String SourceFile      = "";
        int    sourceFileIndex = Bytes.toInt(data, attributeOffset);
        if (sourceFileIndex > 0) {
            int sourceFileOffset = pool[sourceFileIndex];
            SourceFile = new String(data, sourceFileOffset + 3, Bytes.toInt(data, sourceFileOffset + 1));
        }
        return SourceFile;
    }
}
