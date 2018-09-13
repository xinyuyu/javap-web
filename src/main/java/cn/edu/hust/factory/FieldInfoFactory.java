package cn.edu.hust.factory;

import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.service.wrap.ClassFileBlockUtil;

public class FieldInfoFactory {
    private static BlockInfo[] createFieldBase(int zoneStart, byte[] data, int[] pool) {
        BlockInfo[] fieldInfoContent = new BlockInfo[5];
        RangeInfo accessFlagRange = RangeInfoFactory.getRangeInfo(zoneStart, zoneStart + 1, "field access flag");
        BlockInfo accessFlagBlock = BlockInfoFactory.getBlockInfo(accessFlagRange);
        fieldInfoContent[0] = accessFlagBlock;

        RangeInfo nameIndexRange = RangeInfoFactory.getRangeInfo(zoneStart + 2, zoneStart + 3, "field name index");
        BlockInfo nameIndexUtf8Block = ClassFileBlockUtil.getConstantItemBlock(data, pool, pool[Bytes.toInt(data, zoneStart + 2)]);
        nameIndexUtf8Block.getRangeInfo().setParent(nameIndexRange.getStart());
        BlockInfo nameIndexBlock = BlockInfoFactory.getBlockInfo(nameIndexRange, nameIndexUtf8Block);
        fieldInfoContent[1] = nameIndexBlock;

        RangeInfo descriptionIndexRange = RangeInfoFactory.getRangeInfo(zoneStart + 4, zoneStart + 5, "description index");
        BlockInfo descriptionIndexUtf8Block = ClassFileBlockUtil.getConstantItemBlock(data, pool, pool[Bytes.toInt(data, zoneStart + 4)]);
        BlockInfo descriptionIndexBlock = BlockInfoFactory.getBlockInfo(descriptionIndexRange, descriptionIndexUtf8Block);
        fieldInfoContent[2] = descriptionIndexBlock;

        RangeInfo attributeAccountRange = RangeInfoFactory.getRangeInfo(zoneStart + 6, zoneStart + 7, "attribute count");
        BlockInfo attributeBlock = BlockInfoFactory.getBlockInfo(attributeAccountRange);
        fieldInfoContent[3] = attributeBlock;
        return fieldInfoContent;
    }

    private static BlockInfo[] createAttBase(int zoneStart, byte[] data, int[] pool) {
        BlockInfo[] attContents = new BlockInfo[3];
        RangeInfo attIndexNameRange = RangeInfoFactory.getRangeInfo(zoneStart + 8, zoneStart + 9, "attribute name");
        int index = Bytes.toInt(data, attIndexNameRange.getStart());
        BlockInfo attIndexNameUtf8Block = ClassFileBlockUtil.getConstantItemBlock(data, pool, pool[index]);
        BlockInfo attIndexNameBlock = BlockInfoFactory.getBlockInfo(attIndexNameRange, attIndexNameUtf8Block);
        attContents[0] = attIndexNameBlock;

        RangeInfo attLengthRange = RangeInfoFactory.getRangeInfo(zoneStart + 10, zoneStart + 13, "attribute length");
        BlockInfo attLengthBlock = BlockInfoFactory.getBlockInfo(attLengthRange);
        attContents[1] = attLengthBlock;

        int attributesLength = Bytes.toInt(data, zoneStart + 10, 4);
        RangeInfo attInfoRange = RangeInfoFactory.getRangeInfo(zoneStart + 14, zoneStart + 14 + attributesLength - 1, "attribute info");
        BlockInfo attInfoBlock = BlockInfoFactory.getBlockInfo(attInfoRange);
        attContents[2] = attInfoBlock;

        return attContents;
    }

    public static BlockInfo getFieldInfo(byte[] data, int[] pool) {
        int zoneStart = ClassFile.getFieldsZoneOffset(data, pool) + 2;
        int filedCount = Bytes.toInt(data, ClassFile.getFieldsZoneOffset(data, pool));
        BlockInfo[] fieldsBlock = new BlockInfo[filedCount];
        int fieldBlockInfoStart = zoneStart;
        for (int i = 0; i < filedCount; i++) {
            int start = zoneStart;
            BlockInfo baseBlocks[] = createFieldBase(zoneStart, data, pool);
            int attributeCount = Bytes.toInt(data, zoneStart + 6);
            int attBlockInfoStart = zoneStart + 8;
            BlockInfo[] attBlocks = new BlockInfo[attributeCount];
            for (int j = 0; j < attributeCount; j++) {
                BlockInfo[] attContents = createAttBase(zoneStart, data, pool);
                int attributesLength = Bytes.toInt(data, zoneStart + 10, 4);
                zoneStart += (14 + attributesLength);
                RangeInfo attRange = RangeInfoFactory.getRangeInfo(attBlockInfoStart, zoneStart - 1, "filed attribute zone");
                attBlocks[j] = BlockInfoFactory.getBlockInfo(attRange, attContents);
            }
            RangeInfo attRange = RangeInfoFactory.getRangeInfo(attBlockInfoStart, zoneStart, "field attribute zone");
            BlockInfo attBlockInfo = BlockInfoFactory.getBlockInfo(attRange, attBlocks);
            baseBlocks[4] = attBlockInfo;
            RangeInfo fieldRange = RangeInfoFactory.getRangeInfo(start, zoneStart, "field zone");
            start = zoneStart;
            fieldsBlock[i] = BlockInfoFactory.getBlockInfo(fieldRange, baseBlocks);
        }
        RangeInfo fieldRange = RangeInfoFactory.getRangeInfo(fieldBlockInfoStart - 2, zoneStart, "field zone");
        return BlockInfoFactory.getBlockInfo(fieldRange, fieldsBlock);
    }
}
