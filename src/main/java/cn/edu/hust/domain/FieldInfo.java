package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import cn.edu.hust.utils.FileUtils;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class FieldInfo {
    private RangeInfo accessFlags; // u2
    private BlockInfo nameIndex; // u2
    private BlockInfo descriptorIndex; // u2
    private RangeInfo attributeCount; // u2
    private Attribute[] attributes;


    public void init(RangeInfo accessFlags, BlockInfo nameIndex, BlockInfo descriptorIndex, RangeInfo attributeCount, Attribute[] attributes) {
        this.accessFlags = accessFlags;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributeCount = attributeCount;
        this.attributes = attributes;
    }

    public FieldInfo(byte[] data, int[] pool, int offset) {
        RangeInfo accessFlags = RangeInfoFactory.getRangeInfo(offset, Description.FI_ACCESS_INDEX);

        RangeInfo nameIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset + 2, Description.FI_NAME_INDEX);
        BlockInfo nameIndex = BlockInfoFactory.getBlockInfo(nameIndexRangeInfo, data, pool);

        RangeInfo descriptorIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset + 4, Description.FI_DESCRIPTOR_INDEX);
        BlockInfo descriptorIndex = BlockInfoFactory.getBlockInfo(descriptorIndexRangeInfo, data, pool);

        RangeInfo attributeCount = RangeInfoFactory.getRangeInfo(offset + 6, Description.FI_ATT_COUNT);
        int arrLength = Bytes.toIntRange(data, attributeCount.getStart(), attributeCount.getEnd());
        Attribute[] attributes = new Attribute[arrLength];
        offset += 8;
        for (int i = 0; i < arrLength; i++) {
            attributes[i] = Attribute.getAttributeInstance(data, pool, offset);
            offset += attributes[i].getLength(data);
        }
        init(accessFlags, nameIndex, descriptorIndex, attributeCount, attributes);
    }

    public long getLength(byte[] data) {
        long length = 0;
        for (Attribute attribute : attributes) {
            length += attribute.getLength(data);
        }
        return length + 8;
    }

    public static void main(String[] args) throws IOException {
        byte[] data = FileUtils.getFileByteData("D:\\GitRep\\javap-web-gitrep\\src\\main\\resources\\C.class");
        int[] pool = ClassFile.getConstantPool(data);
        FieldInfo fi = new FieldInfo(data, pool, 388);

        System.out.println(fi);
    }
}
