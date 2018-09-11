package cn.edu.hust.service.wrap;


import cn.edu.hust.dto.BlockInfoDto;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.engine.api.Tag;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;

import java.io.*;
import java.util.*;

public class ClassFileBlockUtil {

    public static BlockInfoDto getMagicBlock() {
        RangeInfo magicRangeInfo = RangeInfoFactory.getRangeInfo(0, 3, "magic number");
        return BlockInfoFactory.getBlockInfoDto(magicRangeInfo);
    }

    public static BlockInfoDto getVersionBlock(byte[] data) {
        String version = "major: " + Bytes.toInt(data, 6, 2) + " minor: " + Bytes.toInt(data, 4);
        RangeInfo versionRangeInfo = RangeInfoFactory.getRangeInfo(4, 7, version);
        return BlockInfoFactory.getBlockInfoDto(versionRangeInfo);
    }

    public static BlockInfoDto getPoolSizeBlock(int[] pool) {
        RangeInfo poolSizeRangeInfo = RangeInfoFactory.getRangeInfo(8, 9, "constant pool size");
        String poolZoneDes = "constant pool length from 10 ~" + (pool[0] - 1);
        RangeInfo poolZoneRangeInfo = RangeInfoFactory.getRangeInfo(8, 10, pool[0] - 1, poolZoneDes);
        return BlockInfoFactory.getBlockInfoDto(poolSizeRangeInfo, poolZoneRangeInfo);
    }

    private static BlockInfoDto getConstantItemBlock(byte[] data, int[] pool, int start) {
        int end = -1;
        BlockInfoDto constantItemBlock = null;
        switch (Bytes.toInt(data, start, 1)) {
            case Tag.CONSTANT_UTF8:
                end += start + Bytes.toInt(data, start + 1) + 3;
                String constantItemDes = "CONSTANT_UTF8: " + new String(data, start + 3, Bytes.toInt(data, start + 1));
                RangeInfo utf8RangeInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(utf8RangeInfo);
                break;
            case Tag.CONSTANT_STRING:
                end += start + 1 + 2;
                BlockInfoDto next = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                next.getRangeInfo().setParent(start);
                constantItemDes = "CONSTANT_STRING: " + next.getRangeInfo().getDescription();
                RangeInfo stringRangeInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(stringRangeInfo, next);
                break;
            case Tag.CONSTANT_CLASS:
                end += start + 1 + 2;
                next = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                next.getRangeInfo().setParent(start);
                constantItemDes = "CONSTANT_CLASS: " + next.getRangeInfo().getDescription();
                RangeInfo classInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(classInfo, next);
                break;
            case Tag.CONSTANT_METHOD_TYPE:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_METHOD_HANDLE:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_INTEGER:
                end += start + 1 + 2 + 2;
                RangeInfo integerRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_INTEGER");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(integerRangeInfo);
                break;
            case Tag.CONSTANT_FLOAT:
                end += start + 1 + 2 + 2;
                RangeInfo floatRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_FLOAT");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(floatRangeInfo);
                break;
            case Tag.CONSTANT_FIELD_REF:
                end += start + 1 + 2 + 2;
                BlockInfoDto index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                BlockInfoDto index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo fieldRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_FIELD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(fieldRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_METHOD_REF:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo methodRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_METHOD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(methodRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_INTERFACE_METHOD_REF:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo interfaceMethodRefRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_INTERFACE_METHOD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(interfaceMethodRefRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_NAME_AND_TYPE:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo nameAdTypeRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_NAME_AND_TYPE");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(nameAdTypeRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_INVOKE_DYNAMIC:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_LONG:
                end += start + 1 + 4 + 4;
                RangeInfo longRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_LONG");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(longRangeInfo);
                break;
            case Tag.CONSTANT_DOUBLE:
                end += start + 1 + 4 + 4;
                RangeInfo doubleRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_DOUBLE");
                constantItemBlock = BlockInfoFactory.getBlockInfoDto(doubleRangeInfo);
                break;
        }
        return constantItemBlock;
    }

    public static BlockInfoDto[] getConstantItemBlockArray(byte[] data, int[] pool) {
        BlockInfoDto[] constantItems = new BlockInfoDto[pool.length];
        for (int i = 1; i < pool.length; i++) {
            constantItems[i] = getConstantItemBlock(data, pool, pool[i]);
        }
        constantItems[0] = BlockInfoFactory.getBlockInfoDto(RangeInfoFactory.getRangeInfo(-1, -1, ""));
        return constantItems;
    }

    public static BlockInfoDto getAccessBlock(int[] pool) {
        RangeInfo accessBlock = RangeInfoFactory.getRangeInfo(pool[0], pool[0] + 1, "Access Flags");
        return BlockInfoFactory.getBlockInfoDto(accessBlock);
    }

    public static BlockInfoDto getThisClassBlock(byte[] data, int[] pool, BlockInfoDto[] constantItemsBlock) {
        int index = Bytes.toInt(data, pool[0] + 2);
        BlockInfoDto constantItemBlock = constantItemsBlock[index];
        constantItemBlock.getRangeInfo().setParent(pool[0] + 2);
        String thisClassDes = "this class: " + constantItemBlock.getRangeInfo().getDescription();
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 2, pool[0] + 3, thisClassDes);
        return BlockInfoFactory.getBlockInfoDto(rangeInfo, constantItemBlock);
    }

    public static BlockInfoDto getSuperClassBlock(byte[] data, int[] pool, BlockInfoDto[] constantItemsBlock) {
        int index = Bytes.toInt(data, pool[0] + 4);
        BlockInfoDto constantItemBlock = constantItemsBlock[index];
        constantItemBlock.getRangeInfo().setParent(pool[0] + 4);
        String superClassDes = "super class: " + constantItemBlock.getRangeInfo().getDescription();
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 4, pool[0] + 5, superClassDes);
        return BlockInfoFactory.getBlockInfoDto(rangeInfo, constantItemBlock);
    }

    public static BlockInfoDto getInterfaceCountBlock(byte[] data, int[] pool) {
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 6, pool[0] + 7, "Interface Count");
        return BlockInfoFactory.getBlockInfoDto(rangeInfo);
    }

    public static BlockInfoDto[] getInterfacesBlock(byte[] data, int[] pool) {
        int startOffset = pool[0] + 8;
        int interfaceCount = Bytes.toInt(data, pool[0] + 6);
        BlockInfoDto[] interfaceBlockInfo = new BlockInfoDto[interfaceCount];
        for (int i = 0; i < interfaceCount; i++) {
            BlockInfoDto next = getConstantItemBlock(data, pool, interfaceCount);
            String interfaceDes = "interface" + i + ": " + next.getRangeInfo().getDescription();
            RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(startOffset, startOffset + 2, interfaceDes);
            interfaceBlockInfo[i] = BlockInfoFactory.getBlockInfoDto(rangeInfo, next);
            startOffset += 3;
        }
        return interfaceBlockInfo;
    }

    public static BlockInfoDto getFieldCountBlock(byte[] data, int[] pool) {
        int fieldsZoneOffset = ClassFile.getFieldsZoneOffset(data, pool);
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(fieldsZoneOffset, fieldsZoneOffset + 1, "field Count");
        return BlockInfoFactory.getBlockInfoDto(rangeInfo);
    }

    public static BlockInfoDto getFieldZoneBlock(byte[] data, int[] pool) {
        int fieldZoneStartOffset = ClassFile.getFieldsZoneOffset(data, pool) + 2;
        int filedCount = Bytes.toInt(data, ClassFile.getFieldsZoneOffset(data, pool));
        BlockInfoDto[] fieldsBlock = new BlockInfoDto[filedCount];
        int fieldBlockInfoStart = fieldZoneStartOffset;
        for (int i = 0; i < filedCount; i++) {
            BlockInfoDto[] fieldCombox = new BlockInfoDto[5];
            RangeInfo accessFlagRangeInfo = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset, fieldZoneStartOffset + 1, "field access flag");
            BlockInfoDto accessFlagBlockInfo = BlockInfoFactory.getBlockInfoDto(accessFlagRangeInfo);
            fieldCombox[0] = accessFlagBlockInfo;

            RangeInfo nameIndexRangeInfo = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 2, fieldZoneStartOffset + 3, "field name index");
            BlockInfoDto nameIndexUtf8Block = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, fieldZoneStartOffset + 2)]);
            BlockInfoDto nameIndexBlockInfo = BlockInfoFactory.getBlockInfoDto(nameIndexRangeInfo, nameIndexUtf8Block);
            fieldCombox[1] = nameIndexBlockInfo;

            RangeInfo descriptionIndexInfo = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 4, fieldZoneStartOffset + 5, "description index");
            BlockInfoDto descriptionIndexUtf8Block = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, fieldZoneStartOffset + 4)]);
            BlockInfoDto descriptionIndexBlockInfo = BlockInfoFactory.getBlockInfoDto(descriptionIndexInfo, descriptionIndexUtf8Block);
            fieldCombox[2] = descriptionIndexBlockInfo;

            RangeInfo attributeAccountRangeInfo = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 6, fieldZoneStartOffset + 7, "attribute count");
            BlockInfoDto attributeBlockInfo = BlockInfoFactory.getBlockInfoDto(attributeAccountRangeInfo);
            fieldCombox[3] = attributeBlockInfo;

            int attributeCount = Bytes.toInt(data, fieldZoneStartOffset + 6);
            int attBlockInfoStart = fieldZoneStartOffset + 8;
            BlockInfoDto[] attBlocks = new BlockInfoDto[attributeCount];
            for (int j = 0; j < attributeCount; j++) {
                BlockInfoDto[] attCombox = new BlockInfoDto[3];
                RangeInfo attIndexNameRange = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 8, fieldZoneStartOffset + 9, "attribute name");
                BlockInfoDto attIndexNameUtf8Block = getConstantItemBlock(data, pool, fieldZoneStartOffset + 8);
                BlockInfoDto attIndexNameBlock = BlockInfoFactory.getBlockInfoDto(attIndexNameRange, attIndexNameUtf8Block);
                attCombox[0] = attIndexNameBlock;

                RangeInfo attLengthRange = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 10, fieldBlockInfoStart + 13, "attribute length");
                BlockInfoDto attLengthBlock = BlockInfoFactory.getBlockInfoDto(attLengthRange);
                attCombox[1] = attLengthBlock;

                int attributesLength = Bytes.toInt(data, fieldZoneStartOffset + 10, 4);
                RangeInfo attInfoRange = RangeInfoFactory.getRangeInfo(fieldZoneStartOffset + 14, fieldZoneStartOffset + 14 + attributesLength, "attribute info");
                BlockInfoDto attInfoBlock = BlockInfoFactory.getBlockInfoDto(attInfoRange);
                attCombox[2] = attInfoBlock;

                fieldZoneStartOffset += (14 + attributesLength);
                RangeInfo attRange = RangeInfoFactory.getRangeInfo(attBlockInfoStart, fieldZoneStartOffset, "filed attribute zone");
                attBlocks[j] = BlockInfoFactory.getBlockInfoDto(attRange, attCombox);
            }
            RangeInfo attRange = RangeInfoFactory.getRangeInfo(attBlockInfoStart, fieldZoneStartOffset, "field attribute zone");
            BlockInfoDto attBlockInfo = BlockInfoFactory.getBlockInfoDto(attRange, attBlocks);
            fieldCombox[4] = attBlockInfo;

            RangeInfo fieldRange = RangeInfoFactory.getRangeInfo(fieldBlockInfoStart, fieldZoneStartOffset, "field zone");
            fieldsBlock[i] = BlockInfoFactory.getBlockInfoDto(fieldRange, fieldCombox);
        }
        RangeInfo fieldRange = RangeInfoFactory.getRangeInfo(fieldBlockInfoStart, fieldZoneStartOffset, "field zone");
        return BlockInfoFactory.getBlockInfoDto(fieldRange, fieldsBlock);
    }

    public static List<BlockInfoDto> getClassFileBlockInfo(byte[] data, int[] pool) {
        List<BlockInfoDto> list = new ArrayList<>();

        list.add(ClassFileBlockUtil.getMagicBlock());
        list.add(ClassFileBlockUtil.getVersionBlock(data));
        list.add(ClassFileBlockUtil.getPoolSizeBlock(pool));
        BlockInfoDto[] constantItemsBlock = ClassFileBlockUtil.getConstantItemBlockArray(data, pool);
        list.addAll(Arrays.asList(constantItemsBlock));
        list.add(ClassFileBlockUtil.getAccessBlock(pool));
        list.add(ClassFileBlockUtil.getThisClassBlock(data, pool, constantItemsBlock));
        list.add(ClassFileBlockUtil.getSuperClassBlock(data, pool, constantItemsBlock));
        list.add(ClassFileBlockUtil.getInterfaceCountBlock(data, pool));
        list.add(ClassFileBlockUtil.getInterfaceCountBlock(data, pool));
        list.add(ClassFileBlockUtil.getFieldCountBlock(data, pool));
        list.add(ClassFileBlockUtil.getFieldZoneBlock(data, pool));
        return list;
    }

    public static Map<Integer, BlockInfoDto> offsetStartToBlockInfo(List<BlockInfoDto> list) {
        Map<Integer, BlockInfoDto> map = new HashMap<>();
        for (BlockInfoDto blockInfoDto : list) {
            map.put(blockInfoDto.getRangeInfo().getStart(), blockInfoDto);
        }
        return map;
    }

    public static List<RangeInfo> getClassFileBlockStartOffset(byte[] data, int[] pool) {
        List<RangeInfo> result = new ArrayList<>();
        List<BlockInfoDto> list = getClassFileBlockInfo(data, pool);
        BlockInfoDto[] blockInfoDtos = new BlockInfoDto[1];
        for (int i = 0; i < list.size(); i++) {
            blockInfoDtos[0] = list.get(i);
            result.addAll(getClassFileRangeInternal(blockInfoDtos));
        }
        return result;
    }

    private static List<RangeInfo> getClassFileRangeInternal(BlockInfoDto[] blockInfoDtos) {
        List<RangeInfo> result = new ArrayList<>();
        for (BlockInfoDto b : blockInfoDtos) {
            if (b != null) {
                result.add(b.getRangeInfo());
                if (b.getNexts() != null && b.getNexts().length != 0) {
                    result.addAll(getClassFileRangeInternal(b.getNexts()));
                }
            }
        }
        return result;
    }

    public static List<RangeInfo> getClassFileBlockByStartOffset(byte[] data, int[] pool, int offset) {
        List<BlockInfoDto> allBlock = getClassFileBlockInfo(data, pool);
        Map<Integer, BlockInfoDto> offsetStartToBlockInfo = offsetStartToBlockInfo(allBlock);
        BlockInfoDto findBlockInfo = offsetStartToBlockInfo.get(offset);
        findBlockInfo.getRangeInfo().setParent(-1);
        List<RangeInfo> result = new ArrayList<>();
        if (findBlockInfo != null) {
            result.add(findBlockInfo.getRangeInfo());
            if (findBlockInfo.getNexts() != null && findBlockInfo.getNexts().length != 0) {
                result.addAll(getClassFileRangeInternal(findBlockInfo.getNexts()));
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream(new File(ClassFileBlockUtil.class.getResource("/").getPath() + "C.class"));
        byte[] data = new byte[1024 * 10];
        in.read(data);
        int[] pool = ClassFile.getConstantPool(data);

//        System.out.println(ClassFileBlockUtil.getMagicBlock());
//        System.out.println(ClassFileBlockUtil.getVersionBlock(data));
//        System.out.println(ClassFileBlockUtil.getPoolSizeBlock(pool));
//
//
//        BlockInfoDto[] constantItemsBlock =constantItemsBlock ClassFileBlockUtil.getConstantItemBlockArray(data, pool);
        /*
        for (BlockInfoDto blockInfoDto : constantItemsBlock) {
            System.out.println(blockInfoDto);
        }
        */
//        System.out.println(ClassFileOffsetUtil.getAccessBlock(pool));
//        System.out.println(ClassFileOffsetUtil.getThisClassBlock(data, pool, constantItemsBlock));
//        System.out.println(ClassFileOffsetUtil.getSuperClassBlock(data, pool, constantItemsBlock));
//
//        System.out.println(ClassFileOffsetUtil.getInterfaceCountBlock(data, pool));
//        System.out.println(ClassFileOffsetUtil.getFieldCountBlock(data, pool));

//        BlockInfoDto fieldInfoBlocks = ClassFileBlockUtil.getFieldZoneBlock(data, pool);
//        System.out.println(fieldInfoBlocks);

//        System.out.println(getClassFileBlockInfo(data, pool));

        System.out.println(getClassFileBlockStartOffset(data, pool));
    }
}
