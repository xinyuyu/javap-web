package cn.edu.hust.service.wrap;


import cn.edu.hust.domain.Attribute;
import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.domain.FieldInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.engine.api.Tag;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.FieldInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import cn.edu.hust.utils.TransUtils;

import java.io.*;
import java.util.*;

public class ClassFileBlockUtil {

    public static BlockInfo getMagicBlock() {
        RangeInfo magicRangeInfo = RangeInfoFactory.getRangeInfo(0, 3, "magic number");
        return BlockInfoFactory.getBlockInfo(magicRangeInfo);
    }

    public static BlockInfo getVersionBlock(byte[] data) {
        String version = "major: " + Bytes.toInt(data, 6, 2) + " minor: " + Bytes.toInt(data, 4);
        RangeInfo versionRangeInfo = RangeInfoFactory.getRangeInfo(4, 7, version);
        return BlockInfoFactory.getBlockInfo(versionRangeInfo);
    }

    public static BlockInfo getPoolSizeBlock(int[] pool) {
        RangeInfo poolSizeRangeInfo = RangeInfoFactory.getRangeInfo(8, 9, "constant pool size");
        String poolZoneDes = "constant pool length from 10 ~" + (pool[0] - 1);
        RangeInfo poolZoneRangeInfo = RangeInfoFactory.getRangeInfo(8, 10, pool[0] - 1, poolZoneDes);
        return BlockInfoFactory.getBlockInfo(poolSizeRangeInfo, poolZoneRangeInfo);
    }

    public static BlockInfo getConstantItemBlock(byte[] data, int[] pool, int start) {
        int end = -1;
        BlockInfo constantItemBlock = null;
        switch (Bytes.toInt(data, start, 1)) {
            case Tag.CONSTANT_UTF8:
                end += start + Bytes.toInt(data, start + 1) + 3;
                String constantItemDes = "CONSTANT_UTF8: " + new String(data, start + 3, Bytes.toInt(data, start + 1));
                RangeInfo utf8RangeInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfo(utf8RangeInfo);
                break;
            case Tag.CONSTANT_STRING:
                end += start + 1 + 2;
                BlockInfo next = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                next.getRangeInfo().setParent(start);
                constantItemDes = "CONSTANT_STRING: " + next.getRangeInfo().getDescription();
                RangeInfo stringRangeInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfo(stringRangeInfo, next);
                break;
            case Tag.CONSTANT_CLASS:
                end += start + 1 + 2;
                next = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                next.getRangeInfo().setParent(start);
                constantItemDes = "CONSTANT_CLASS: " + next.getRangeInfo().getDescription();
                RangeInfo classInfo = RangeInfoFactory.getRangeInfo(start, end, constantItemDes);
                constantItemBlock = BlockInfoFactory.getBlockInfo(classInfo, next);
                break;
            case Tag.CONSTANT_METHOD_TYPE:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_METHOD_HANDLE:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_INTEGER:
                end += start + 1 + 2 + 2;
                RangeInfo integerRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_INTEGER");
                constantItemBlock = BlockInfoFactory.getBlockInfo(integerRangeInfo);
                break;
            case Tag.CONSTANT_FLOAT:
                end += start + 1 + 2 + 2;
                RangeInfo floatRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_FLOAT");
                constantItemBlock = BlockInfoFactory.getBlockInfo(floatRangeInfo);
                break;
            case Tag.CONSTANT_FIELD_REF:
                end += start + 1 + 2 + 2;
                BlockInfo index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                BlockInfo index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo fieldRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_FIELD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfo(fieldRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_METHOD_REF:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo methodRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_METHOD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfo(methodRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_INTERFACE_METHOD_REF:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo interfaceMethodRefRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_INTERFACE_METHOD_REF");
                constantItemBlock = BlockInfoFactory.getBlockInfo(interfaceMethodRefRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_NAME_AND_TYPE:
                end += start + 1 + 2 + 2;
                index1 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 1)]);
                index1.getRangeInfo().setParent(start + 1);
                index2 = getConstantItemBlock(data, pool, pool[Bytes.toInt(data, start + 3)]);
                index2.getRangeInfo().setParent(start + 3);
                RangeInfo nameAdTypeRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_NAME_AND_TYPE");
                constantItemBlock = BlockInfoFactory.getBlockInfo(nameAdTypeRangeInfo, index1, index2);
                break;
            case Tag.CONSTANT_INVOKE_DYNAMIC:// TODO 暂时未分析
                break;
            case Tag.CONSTANT_LONG:
                end += start + 1 + 4 + 4;
                RangeInfo longRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_LONG");
                constantItemBlock = BlockInfoFactory.getBlockInfo(longRangeInfo);
                break;
            case Tag.CONSTANT_DOUBLE:
                end += start + 1 + 4 + 4;
                RangeInfo doubleRangeInfo = RangeInfoFactory.getRangeInfo(start, end, "CONSTANT_DOUBLE");
                constantItemBlock = BlockInfoFactory.getBlockInfo(doubleRangeInfo);
                break;
        }
        return constantItemBlock;
    }

    public static BlockInfo[] getConstantItemBlockArray(byte[] data, int[] pool) {
        BlockInfo[] constantItems = new BlockInfo[pool.length];
        for (int i = 1; i < pool.length; i++) {
            constantItems[i] = getConstantItemBlock(data, pool, pool[i]);
        }
        constantItems[0] = BlockInfoFactory.getBlockInfo(RangeInfoFactory.getRangeInfo(-1, -1, ""));
        return constantItems;
    }

    public static BlockInfo getAccessBlock(int[] pool) {
        RangeInfo accessBlock = RangeInfoFactory.getRangeInfo(pool[0], pool[0] + 1, "Access Flags");
        return BlockInfoFactory.getBlockInfo(accessBlock);
    }

    public static BlockInfo getThisClassBlock(byte[] data, int[] pool, BlockInfo[] constantItemsBlock) {
        int index = Bytes.toInt(data, pool[0] + 2);
        BlockInfo constantItemBlock = constantItemsBlock[index];
        constantItemBlock.getRangeInfo().setParent(pool[0] + 2);
        String thisClassDes = "this class: " + constantItemBlock.getRangeInfo().getDescription();
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 2, pool[0] + 3, thisClassDes);
        return BlockInfoFactory.getBlockInfo(rangeInfo, constantItemBlock);
    }

    public static BlockInfo getSuperClassBlock(byte[] data, int[] pool, BlockInfo[] constantItemsBlock) {
        int index = Bytes.toInt(data, pool[0] + 4);
        BlockInfo constantItemBlock = constantItemsBlock[index];
        constantItemBlock.getRangeInfo().setParent(pool[0] + 4);
        String superClassDes = "super class: " + constantItemBlock.getRangeInfo().getDescription();
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 4, pool[0] + 5, superClassDes);
        return BlockInfoFactory.getBlockInfo(rangeInfo, constantItemBlock);
    }

    public static BlockInfo getInterfaceCountBlock(byte[] data, int[] pool) {
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(pool[0] + 6, pool[0] + 7, "Interface Count");
        return BlockInfoFactory.getBlockInfo(rangeInfo);
    }

    public static BlockInfo[] getInterfacesBlock(byte[] data, int[] pool) {
        int startOffset = pool[0] + 8;
        int interfaceCount = Bytes.toInt(data, pool[0] + 6);
        BlockInfo[] interfaceBlockInfo = new BlockInfo[interfaceCount];
        for (int i = 0; i < interfaceCount; i++) {
            BlockInfo next = getConstantItemBlock(data, pool, interfaceCount);
            String interfaceDes = "interface" + i + ": " + next.getRangeInfo().getDescription();
            RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(startOffset, startOffset + 2, interfaceDes);
            interfaceBlockInfo[i] = BlockInfoFactory.getBlockInfo(rangeInfo, next);
            startOffset += 3;
        }
        return interfaceBlockInfo;
    }

    public static BlockInfo getFieldCountBlock(byte[] data, int[] pool) {
        int fieldsZoneOffset = ClassFile.getFieldsZoneOffset(data, pool);
        RangeInfo rangeInfo = RangeInfoFactory.getRangeInfo(fieldsZoneOffset, fieldsZoneOffset + 1, "field Count");
        return BlockInfoFactory.getBlockInfo(rangeInfo);
    }

    public static BlockInfo getFieldZoneBlock(byte[] data, int[] pool) {
        return FieldInfoFactory.getFieldInfo(data, pool);
    }

    public static BlockInfo getFieldZoneBlockNew(byte[] data, int[] pool) throws IllegalAccessException {
        int offset = ClassFile.getFieldsZoneOffset(data, pool) + 2;
        int start = offset;
        int filedCount = Bytes.toInt(data, ClassFile.getFieldsZoneOffset(data, pool));
        FieldInfo[] fieldInfoArr = new FieldInfo[filedCount];
        List<BlockInfo> allBlocksInField = new ArrayList<>();
        for (int i = 0; i < filedCount; i++) {
            fieldInfoArr[i] = new FieldInfo(data, pool, offset);
            allBlocksInField.addAll(TransUtils.fieldTransToBlockInfo(fieldInfoArr[i]));
            offset += fieldInfoArr[i].getLength(data);
        }
        RangeInfo fieldBlockIndex = RangeInfoFactory.getRangeInfo(start, offset, "Field zone");
        BlockInfo [] blockInfoArr = new BlockInfo[allBlocksInField.size()];
        allBlocksInField.toArray(blockInfoArr);
        BlockInfo fieldBlock = BlockInfoFactory.getBlockInfo(fieldBlockIndex,blockInfoArr);
        System.out.println(fieldInfoArr);
        return null;
    }

    public static List<BlockInfo> getClassFileBlockInfo(byte[] data, int[] pool) {
        List<BlockInfo> list = new ArrayList<>();

        list.add(ClassFileBlockUtil.getMagicBlock());
        list.add(ClassFileBlockUtil.getVersionBlock(data));
        list.add(ClassFileBlockUtil.getPoolSizeBlock(pool));
        BlockInfo[] constantItemsBlock = ClassFileBlockUtil.getConstantItemBlockArray(data, pool);
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

    public static Map<Integer, BlockInfo> offsetStartToBlockInfo(List<BlockInfo> list) {
        Map<Integer, BlockInfo> map = new HashMap<>();
        for (BlockInfo blockInfo : list) {
            map.put(blockInfo.getRangeInfo().getStart(), blockInfo);
            if (blockInfo.getNexts() != null && blockInfo.getNexts().length != 0){
                map.putAll(offsetStartToBlockInfo(Arrays.asList(blockInfo.getNexts())));
            }
        }
        return map;
    }

    public static List<RangeInfo> getClassFileBlockStartOffset(byte[] data, int[] pool) {
        List<RangeInfo> result = new ArrayList<>();
        List<BlockInfo> list = getClassFileBlockInfo(data, pool);
        BlockInfo[] BlockInfos = new BlockInfo[1];
        for (int i = 0; i < list.size(); i++) {
            BlockInfos[0] = list.get(i);
            result.addAll(getClassFileRangeInternal(BlockInfos));
        }
        return result;
    }

    private static List<RangeInfo> getClassFileRangeInternal(BlockInfo[] BlockInfos) {
        List<RangeInfo> result = new ArrayList<>();
        for (BlockInfo b : BlockInfos) {
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
        List<BlockInfo> allBlock = getClassFileBlockInfo(data, pool);
        Map<Integer, BlockInfo> offsetStartToBlockInfo = offsetStartToBlockInfo(allBlock);
        BlockInfo findBlockInfo = offsetStartToBlockInfo.get(offset);
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

    public static void main(String[] args) throws IOException, IllegalAccessException {
        InputStream in = new FileInputStream(new File(ClassFileBlockUtil.class.getResource("/").getPath() + "C.class"));
        byte[] data = new byte[1024 * 10];
        in.read(data);
        int[] pool = ClassFile.getConstantPool(data);
        getFieldZoneBlockNew(data, pool);
//        System.out.println(ClassFileBlockUtil.getMagicBlock());
//        System.out.println(ClassFileBlockUtil.getVersionBlock(data));
//        System.out.println(ClassFileBlockUtil.getPoolSizeBlock(pool));
//
//
//        BlockInfo[] constantItemsBlock =constantItemsBlock ClassFileBlockUtil.getConstantItemBlockArray(data, pool);
        /*
        for (BlockInfo BlockInfo : constantItemsBlock) {
            System.out.println(BlockInfo);
        }
        */
//        System.out.println(ClassFileOffsetUtil.getAccessBlock(pool));
//        System.out.println(ClassFileOffsetUtil.getThisClassBlock(data, pool, constantItemsBlock));
//        System.out.println(ClassFileOffsetUtil.getSuperClassBlock(data, pool, constantItemsBlock));
//
//        System.out.println(ClassFileOffsetUtil.getInterfaceCountBlock(data, pool));
//        System.out.println(ClassFileOffsetUtil.getFieldCountBlock(data, pool));

//        BlockInfo fieldInfoBlocks = ClassFileBlockUtil.getFieldZoneBlock(data, pool);
//        System.out.println(fieldInfoBlocks);

//        System.out.println(getClassFileBlockInfo(data, pool));

        System.out.println(getClassFileBlockStartOffset(data, pool));
    }
}
