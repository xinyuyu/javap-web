package cn.edu.hust.factory;

import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.service.wrap.ClassFileBlockUtil;

public class BlockInfoFactory {
    public static BlockInfo getBlockInfo(RangeInfo rangeInfo) {
        return new BlockInfo(rangeInfo, new BlockInfo[0]);
    }

    public static BlockInfo getBlockInfo(RangeInfo rangeInfo, BlockInfo BlockInfo) {
        BlockInfo[] nexts = new BlockInfo[1];
        nexts[0] = BlockInfo;
        BlockInfo result = getBlockInfo(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfo getBlockInfo(RangeInfo rangeInfo, RangeInfo nextRangeInfo) {
        BlockInfo[] nexts = new BlockInfo[1];
        nexts[0] = getBlockInfo(nextRangeInfo);
        BlockInfo result = getBlockInfo(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfo getBlockInfo(RangeInfo rangeInfo, BlockInfo BlockInfo1, BlockInfo BlockInfo2) {
        BlockInfo[] nexts = new BlockInfo[2];
        nexts[0] = BlockInfo1;
        nexts[1] = BlockInfo2;
        BlockInfo result = getBlockInfo(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfo getBlockInfo(RangeInfo rangeInfo, BlockInfo[] BlockInfo) {
        BlockInfo result = getBlockInfo(rangeInfo);
        result.setNexts(BlockInfo);
        return result;
    }

    public static BlockInfo getBlockInfo(RangeInfo rangeInfo, byte[] data, int[] pool) {
        int offset = Bytes.toIntRange(data, rangeInfo.getStart(), rangeInfo.getEnd());
        BlockInfo constantItemBlock = ClassFileBlockUtil.getConstantItemBlock(data, pool, pool[offset]);
        constantItemBlock.getRangeInfo().setParent(rangeInfo.getStart());
        return getBlockInfo(rangeInfo, constantItemBlock);
    }

}
