package cn.edu.hust.factory;

import cn.edu.hust.dto.BlockInfoDto;
import cn.edu.hust.dto.RangeInfo;

public class BlockInfoFactory {
    public static BlockInfoDto getBlockInfoDto(RangeInfo rangeInfo) {
        return new BlockInfoDto(rangeInfo, new BlockInfoDto[0]);
    }

    public static BlockInfoDto getBlockInfoDto(RangeInfo rangeInfo, BlockInfoDto blockInfoDto) {
        BlockInfoDto[] nexts = new BlockInfoDto[1];
        nexts[0] = blockInfoDto;
        BlockInfoDto result = getBlockInfoDto(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfoDto getBlockInfoDto(RangeInfo rangeInfo, RangeInfo nextRangeInfo) {
        BlockInfoDto[] nexts = new BlockInfoDto[1];
        nexts[0] = getBlockInfoDto(nextRangeInfo);
        BlockInfoDto result = getBlockInfoDto(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfoDto getBlockInfoDto(RangeInfo rangeInfo, BlockInfoDto blockInfoDto1, BlockInfoDto blockInfoDto2) {
        BlockInfoDto[] nexts = new BlockInfoDto[2];
        nexts[0] = blockInfoDto1;
        nexts[1] = blockInfoDto2;
        BlockInfoDto result = getBlockInfoDto(rangeInfo);
        result.setNexts(nexts);
        return result;
    }

    public static BlockInfoDto getBlockInfoDto(RangeInfo rangeInfo, BlockInfoDto [] blockInfoDto) {
        BlockInfoDto result = getBlockInfoDto(rangeInfo);
        result.setNexts(blockInfoDto);
        return result;
    }

}
