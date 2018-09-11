package cn.edu.hust.dto;

import lombok.Data;

@Data
public class BlockInfoDto {
    RangeInfo rangeInfo;
    BlockInfoDto[] nexts;

    public BlockInfoDto(RangeInfo rangeInfo, BlockInfoDto[] nexts) {
        this.rangeInfo = rangeInfo;
        this.nexts = nexts;
    }

    public boolean inRange(int offset){
        return rangeInfo.inRange(offset);
    }
}
