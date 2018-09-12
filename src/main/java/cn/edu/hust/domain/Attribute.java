package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import lombok.Data;

@Data
public class Attribute {
    protected BlockInfo attributeNameIndex; // u2
    protected RangeInfo attributeLength; //u4


    public Attribute(byte[] data, int[] pool, int offset) {
        RangeInfo attributeNameIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset, Description.ATT_NAME_INDEX);
        BlockInfo attributeNameIndex = BlockInfoFactory.getBlockInfo(attributeNameIndexRangeInfo, data, pool);
        RangeInfo attributeLength = RangeInfoFactory.getRangeInfo(offset + 2, Description.ATT_LENGTH);
        this.attributeLength = attributeLength;
        this.attributeNameIndex = attributeNameIndex;
    }

    public long getLength(byte[] data) {
        return 0;
    }

    public String getAttributeName(byte[] data, int[] pool) {
        int index = Bytes.toIntRange(data, attributeNameIndex.getRangeStart(), attributeNameIndex.getRangeEnd());
        int offset = pool[index];
        return new String(data, offset + 3, Bytes.toInt(data, offset + 1));

    }

    public static Attribute getAttributeInstance(byte[] data, int[] pool, int offset) {
        Attribute attribute = new Attribute(data, pool, offset);
        String attributeName = attribute.getAttributeName(data, pool);
        switch (attributeName) {
            case "LOCAL_VARIABLE_TABLE":
                attribute = new LineNumberTable(data, pool, offset);
                break;
            case "LINE_NUMBER_TABLE":
                attribute = new LocalVariableTable(data, pool, offset);
                break;
            case "ConstantValue":
                attribute = new ConstantValue(data, pool, offset);
                break;
            default:
                attribute = new Attribute(data, pool, offset);
        }
        return attribute;
    }
}
